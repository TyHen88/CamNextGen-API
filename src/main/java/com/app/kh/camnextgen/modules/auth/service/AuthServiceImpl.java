package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.auth.domain.RefreshToken;
import com.app.kh.camnextgen.modules.auth.dto.AuthResponse;
import com.app.kh.camnextgen.modules.auth.dto.ForgotPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.LoginRequest;
import com.app.kh.camnextgen.modules.auth.dto.RefreshTokenRequest;
import com.app.kh.camnextgen.modules.auth.dto.RegisterRequest;
import com.app.kh.camnextgen.modules.auth.dto.ResetPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.VerifyEmailRequest;
import com.app.kh.camnextgen.modules.user.domain.Role;
import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.domain.UserRole;
import com.app.kh.camnextgen.modules.user.domain.UserStatus;
import com.app.kh.camnextgen.modules.user.dto.UserResponse;
import com.app.kh.camnextgen.modules.user.repo.RoleRepository;
import com.app.kh.camnextgen.modules.user.repo.UserRepository;
import com.app.kh.camnextgen.modules.user.repo.UserRoleRepository;
import com.app.kh.camnextgen.shared.audit.service.AuditLogService;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import com.app.kh.camnextgen.shared.security.JwtService;
import com.app.kh.camnextgen.shared.security.UserPrincipal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE_CODE = "STUDENT";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final AuditLogService auditLogService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            TokenService tokenService,
            EmailVerificationService emailVerificationService,
            PasswordResetService passwordResetService,
            AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("EMAIL_EXISTS", "Email already registered");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        userRepository.save(user);

        Role role = roleRepository.findByCode(DEFAULT_ROLE_CODE)
                .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "Default role not configured"));
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedAt(Instant.now());
        userRoleRepository.save(userRole);
        user.getUserRoles().add(userRole);

        emailVerificationService.createVerification(user);
        auditLogService.logEvent("USER_REGISTERED", user.getId(), "{\"userId\":" + user.getId() + "}");
        return new AuthResponse(null, null, toUserResponse(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByIdWithRoles(principal.getId())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("USER_NOT_ACTIVE", "Email verification required");
        }
        String accessToken = jwtService.generateAccessToken(principal);
        RefreshToken refreshToken = tokenService.createRefreshToken(user);
        auditLogService.logEvent("USER_LOGIN", user.getId(), "{\"userId\":" + user.getId() + "}");
        return new AuthResponse(accessToken, refreshToken.getToken(), toUserResponse(user));
    }

    @Override
    public void verifyEmail(VerifyEmailRequest request) {
        emailVerificationService.verifyToken(request.token());
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        passwordResetService.createResetToken(request.email());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = tokenService.validateRefreshToken(request.refreshToken());
        tokenService.revokeToken(refreshToken);
        User user = userRepository.findByIdWithRoles(refreshToken.getUser().getId())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));
        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getUserRoles().stream().map(ur -> ur.getRole().getCode()).collect(Collectors.toSet()),
                user.getUserRoles().stream()
                        .flatMap(ur -> ur.getRole().getRolePermissions().stream())
                        .map(rp -> rp.getPermission().getCode())
                        .collect(Collectors.toSet())
        );
        String accessToken = jwtService.generateAccessToken(principal);
        RefreshToken newRefreshToken = tokenService.createRefreshToken(user);
        return new AuthResponse(accessToken, newRefreshToken.getToken(), toUserResponse(user));
    }

    private UserResponse toUserResponse(User user) {
        Set<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getCode())
                .collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getStatus().name(), roles);
    }
}
