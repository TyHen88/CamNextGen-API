package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.audit.service.AuditLogService;
import com.app.kh.camnextgen.modules.auth.domain.PasswordResetToken;
import com.app.kh.camnextgen.modules.auth.domain.RefreshToken;
import com.app.kh.camnextgen.modules.auth.domain.VerificationToken;
import com.app.kh.camnextgen.modules.auth.dto.AuthResponse;
import com.app.kh.camnextgen.modules.auth.dto.ForgotPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.LoginRequest;
import com.app.kh.camnextgen.modules.auth.dto.RefreshTokenRequest;
import com.app.kh.camnextgen.modules.auth.dto.RegisterRequest;
import com.app.kh.camnextgen.modules.auth.dto.ResetPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.VerifyEmailRequest;
import com.app.kh.camnextgen.modules.auth.repository.PasswordResetTokenRepository;
import com.app.kh.camnextgen.modules.auth.repository.RefreshTokenRepository;
import com.app.kh.camnextgen.modules.auth.repository.VerificationTokenRepository;
import com.app.kh.camnextgen.modules.user.domain.Role;
import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.domain.UserRole;
import com.app.kh.camnextgen.modules.user.domain.UserStatus;
import com.app.kh.camnextgen.modules.user.repository.RoleRepository;
import com.app.kh.camnextgen.modules.user.repository.UserRepository;
import com.app.kh.camnextgen.modules.user.repository.UserRoleRepository;
import com.app.kh.camnextgen.modules.user.service.UserMapper;
import com.app.kh.camnextgen.shared.config.AuthProperties;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import com.app.kh.camnextgen.shared.exception.NotFoundException;
import java.time.Instant;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;
    private final AuthProperties authProperties;
    private final AuditLogService auditLogService;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserRoleRepository userRoleRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           TokenService tokenService,
                           EmailVerificationService emailVerificationService,
                           PasswordResetService passwordResetService,
                           PasswordEncoder passwordEncoder,
                           AuthProperties authProperties,
                           AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
        this.authProperties = authProperties;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("EMAIL_EXISTS", "Email already registered");
        }

        User user = new User();
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setStatus(UserStatus.PENDING);
        userRepository.save(user);

        Role role = roleRepository.findByCode("STUDENT")
            .orElseThrow(() -> new NotFoundException("ROLE_NOT_FOUND", "Default role not found"));
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedAt(Instant.now());
        userRoleRepository.save(userRole);

        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(authProperties.getVerificationTokenTtl()));
        verificationTokenRepository.save(token);
        try {
            emailVerificationService.sendVerification(user, token.getToken());
        }catch (Exception e){
            log.warn("Failed to send verification email to {}: {}", token.getUser().getEmail(), e.getMessage());
        }
        auditLogService.logEvent("USER_REGISTER", user.getId(), "{\"email\":\"" + user.getEmail() + "\"}");

        return new AuthResponse(null, null, UserMapper.toUserResponse(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
            .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid credentials"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("EMAIL_NOT_VERIFIED", "Email not verified");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid credentials");
        }

        String accessToken = tokenService.createAccessToken(user);
        RefreshToken refreshToken = tokenService.createRefreshToken(user);
        auditLogService.logEvent("USER_LOGIN", user.getId(), null);

        return new AuthResponse(accessToken, refreshToken.getToken(), UserMapper.toUserResponse(user));
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        VerificationToken token = verificationTokenRepository.findByToken(request.token())
            .orElseThrow(() -> new BusinessException("INVALID_TOKEN", "Invalid verification token"));
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("TOKEN_EXPIRED", "Verification token expired");
        }
        User user = token.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        verificationTokenRepository.delete(token);
        auditLogService.logEvent("EMAIL_VERIFIED", user.getId(), null);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email().toLowerCase()).ifPresent(user -> {
            PasswordResetToken token = new PasswordResetToken();
            token.setToken(UUID.randomUUID().toString());
            token.setUser(user);
            token.setCreatedAt(Instant.now());
            token.setExpiresAt(Instant.now().plus(authProperties.getPasswordResetTokenTtl()));
            passwordResetTokenRepository.save(token);
            passwordResetService.sendReset(user, token.getToken());
            auditLogService.logEvent("PASSWORD_RESET_REQUEST", user.getId(), null);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.token())
            .orElseThrow(() -> new BusinessException("INVALID_TOKEN", "Invalid reset token"));
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("TOKEN_EXPIRED", "Reset token expired");
        }
        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        passwordResetTokenRepository.delete(token);
        auditLogService.logEvent("PASSWORD_RESET", user.getId(), null);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken existing = refreshTokenRepository.findByToken(request.refreshToken())
            .orElseThrow(() -> new BusinessException("INVALID_TOKEN", "Invalid refresh token"));
        if (existing.isRevoked() || existing.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("TOKEN_EXPIRED", "Refresh token expired");
        }
        RefreshToken rotated = tokenService.rotateRefreshToken(existing);
        User user = rotated.getUser();
        String accessToken = tokenService.createAccessToken(user);
        auditLogService.logEvent("TOKEN_REFRESH", user.getId(), null);
        return new AuthResponse(accessToken, rotated.getToken(), UserMapper.toUserResponse(user));
    }
}
