package com.app.kh.camnextgen.modules.auth.controller;

import com.app.kh.camnextgen.modules.auth.dto.AuthResponse;
import com.app.kh.camnextgen.modules.auth.dto.ForgotPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.LoginRequest;
import com.app.kh.camnextgen.modules.auth.dto.RefreshTokenRequest;
import com.app.kh.camnextgen.modules.auth.dto.RegisterRequest;
import com.app.kh.camnextgen.modules.auth.dto.ResetPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.VerifyEmailRequest;
import com.app.kh.camnextgen.modules.auth.service.AuthService;
import com.app.kh.camnextgen.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest http) {
        return ApiResponse.ok(authService.register(request), requestId(http));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        return ApiResponse.ok(authService.login(request), requestId(http));
    }

    @PostMapping("/verify")
    public ApiResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request, HttpServletRequest http) {
        authService.verifyEmail(request);
        return ApiResponse.ok(null, requestId(http));
    }

    @PostMapping("/forgot")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest http) {
        authService.forgotPassword(request);
        return ApiResponse.ok(null, requestId(http));
    }

    @PostMapping("/reset")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest http) {
        authService.resetPassword(request);
        return ApiResponse.ok(null, requestId(http));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest http) {
        return ApiResponse.ok(authService.refreshToken(request), requestId(http));
    }

    private String requestId(HttpServletRequest request) {
        Object value = request.getAttribute("correlationId");
        return value == null ? null : value.toString();
    }
}
