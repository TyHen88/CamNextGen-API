package com.app.kh.camnextgen.modules.auth.service;

public interface PasswordResetService {
    void createResetToken(String email);

    void resetPassword(String token, String newPassword);
}
