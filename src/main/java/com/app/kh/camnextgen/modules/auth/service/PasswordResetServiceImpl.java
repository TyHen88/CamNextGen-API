package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.auth.domain.PasswordResetToken;
import com.app.kh.camnextgen.modules.auth.repo.PasswordResetTokenRepository;
import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.repo.UserRepository;
import com.app.kh.camnextgen.shared.audit.service.AuditLogService;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final AuditLogService auditLogService;

    public PasswordResetServiceImpl(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailSender emailSender,
            AuditLogService auditLogService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public void createResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS));
        tokenRepository.save(token);
        emailSender.sendEmail(user.getEmail(), "Reset your password", "Reset token: " + token.getToken());
    }

    @Override
    @Transactional
    public void resetPassword(String tokenValue, String newPassword) {
        PasswordResetToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException("INVALID_TOKEN", "Invalid reset token"));
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("TOKEN_EXPIRED", "Reset token expired");
        }
        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(token);
        auditLogService.logEvent("USER_PASSWORD_RESET", user.getId(), "{\"userId\":" + user.getId() + "}");
    }
}
