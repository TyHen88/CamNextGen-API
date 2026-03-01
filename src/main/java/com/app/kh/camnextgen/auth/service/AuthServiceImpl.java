package com.app.kh.camnextgen.auth.service;

import com.app.kh.camnextgen.shared.infra.audit.service.AuditLogService;
import com.app.kh.camnextgen.auth.domain.OtpPurpose;
import com.app.kh.camnextgen.auth.domain.RefreshToken;
import com.app.kh.camnextgen.auth.domain.VerificationToken;
import com.app.kh.camnextgen.auth.dto.AuthResponse;
import com.app.kh.camnextgen.auth.dto.ForgotPasswordRequest;
import com.app.kh.camnextgen.auth.dto.LoginRequest;
import com.app.kh.camnextgen.auth.dto.RefreshTokenRequest;
import com.app.kh.camnextgen.auth.dto.RegisterRequest;
import com.app.kh.camnextgen.auth.dto.ResetPasswordRequest;
import com.app.kh.camnextgen.auth.dto.SendOtpRequest;
import com.app.kh.camnextgen.auth.dto.VerifyEmailRequest;
import com.app.kh.camnextgen.auth.dto.VerifyOtpRequest;
import com.app.kh.camnextgen.auth.repository.RefreshTokenRepository;
import com.app.kh.camnextgen.auth.repository.VerificationTokenRepository;
import com.app.kh.camnextgen.user.domain.Role;
import com.app.kh.camnextgen.user.domain.User;
import com.app.kh.camnextgen.user.domain.UserRole;
import com.app.kh.camnextgen.user.domain.UserStatus;
import com.app.kh.camnextgen.user.repository.RoleRepository;
import com.app.kh.camnextgen.user.repository.UserRepository;
import com.app.kh.camnextgen.user.repository.UserRoleRepository;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import com.app.kh.camnextgen.shared.exception.NotFoundException;
import com.app.kh.camnextgen.shared.security.SecurityUtils;
import java.time.Instant;

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
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final OtpService otpService;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserRoleRepository userRoleRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           TokenService tokenService,
                           PasswordEncoder passwordEncoder,
                           AuditLogService auditLogService,
                           OtpService otpService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
        this.otpService = otpService;
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

        otpService.sendOtp(user.getEmail(), OtpPurpose.EMAIL_VERIFICATION);
        auditLogService.logEvent("USER_REGISTER", user.getId(), "{\"email\":\"" + user.getEmail() + "\"}");

        return new AuthResponse(null, null);
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

        return new AuthResponse(accessToken, refreshToken.getToken());
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
            otpService.sendOtp(user.getEmail(), OtpPurpose.PASSWORD_RESET);
            auditLogService.logEvent("PASSWORD_RESET_REQUEST", user.getId(), null);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.email().trim().toLowerCase();
        otpService.verifyOtp(email, request.otp(), OtpPurpose.PASSWORD_RESET, true);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException("EMAIL_NOT_FOUND", "Email not found"));
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
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
        return new AuthResponse(accessToken, rotated.getToken());
    }

    @Override
    @Transactional
    public void sendOtp(SendOtpRequest request) {
        otpService.sendOtp(request.email(), request.purpose());
    }

    @Override
    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        boolean consumeOnSuccess = request.purpose() != OtpPurpose.PASSWORD_RESET;
        otpService.verifyOtp(request.email(), request.otp(), request.purpose(), consumeOnSuccess);
    }

    @Override
    @Transactional
    public void logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("UNAUTHORIZED", "User not authenticated");
        }
        refreshTokenRepository.revokeAllByUserId(userId);
        auditLogService.logEvent("USER_LOGOUT", userId, null);
        log.info("User logged out successfully: {}", userId);
    }
}
