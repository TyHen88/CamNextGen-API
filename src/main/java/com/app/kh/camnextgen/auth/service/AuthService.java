package com.app.kh.camnextgen.auth.service;

import com.app.kh.camnextgen.auth.dto.AuthResponse;
import com.app.kh.camnextgen.auth.dto.ForgotPasswordRequest;
import com.app.kh.camnextgen.auth.dto.LoginRequest;
import com.app.kh.camnextgen.auth.dto.RefreshTokenRequest;
import com.app.kh.camnextgen.auth.dto.RegisterRequest;
import com.app.kh.camnextgen.auth.dto.ResetPasswordRequest;
import com.app.kh.camnextgen.auth.dto.SendOtpRequest;
import com.app.kh.camnextgen.auth.dto.VerifyEmailRequest;
import com.app.kh.camnextgen.auth.dto.VerifyOtpRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void verifyEmail(VerifyEmailRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void sendOtp(SendOtpRequest request);
    void verifyOtp(VerifyOtpRequest request);
    void logout();
}
