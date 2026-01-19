package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.auth.domain.VerificationToken;
import com.app.kh.camnextgen.modules.auth.repo.VerificationTokenRepository;
import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.domain.UserStatus;
import com.app.kh.camnextgen.modules.user.repo.UserRepository;
import com.app.kh.camnextgen.shared.audit.service.AuditLogService;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final AuditLogService auditLogService;

    public EmailVerificationServiceImpl(
            VerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            EmailSender emailSender,
            AuditLogService auditLogService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public void createVerification(User user) {
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        tokenRepository.save(token);

        String body = "Verify your email with token: " + token.getToken();
        emailSender.sendEmail(user.getEmail(), "Verify your email", body);
    }

    @Override
    @Transactional
    public void verifyToken(String tokenValue) {
        VerificationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException("INVALID_TOKEN", "Invalid verification token"));
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("TOKEN_EXPIRED", "Verification token expired");
        }
        User user = token.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        tokenRepository.delete(token);
        auditLogService.logEvent("USER_EMAIL_VERIFIED", user.getId(), "{\"userId\":" + user.getId() + "}");
    }
}
