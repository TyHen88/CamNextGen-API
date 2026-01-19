package com.app.kh.camnextgen.modules.auth.controller;

import com.app.kh.camnextgen.modules.auth.dto.AuthResponse;
import com.app.kh.camnextgen.modules.auth.dto.ForgotPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.LoginRequest;
import com.app.kh.camnextgen.modules.auth.dto.RefreshTokenRequest;
import com.app.kh.camnextgen.modules.auth.dto.RegisterRequest;
import com.app.kh.camnextgen.modules.auth.dto.ResetPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.VerifyEmailRequest;
import com.app.kh.camnextgen.modules.auth.service.AuthService;
import com.app.kh.camnextgen.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request), requestId());
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request), requestId());
    }

    @PostMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ApiResponse.ok(null, requestId());
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.ok(null, requestId());
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok(null, requestId());
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refreshToken(request), requestId());
    }

    private String requestId() {
        String value = MDC.get("correlationId");
        return value == null ? "" : value;
    }
}
