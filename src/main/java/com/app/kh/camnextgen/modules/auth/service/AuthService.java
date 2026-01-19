package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.auth.dto.AuthResponse;
import com.app.kh.camnextgen.modules.auth.dto.ForgotPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.LoginRequest;
import com.app.kh.camnextgen.modules.auth.dto.RefreshTokenRequest;
import com.app.kh.camnextgen.modules.auth.dto.RegisterRequest;
import com.app.kh.camnextgen.modules.auth.dto.ResetPasswordRequest;
import com.app.kh.camnextgen.modules.auth.dto.VerifyEmailRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void verifyEmail(VerifyEmailRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
