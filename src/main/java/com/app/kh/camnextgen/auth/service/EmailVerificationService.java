package com.app.kh.camnextgen.auth.service;

import com.app.kh.camnextgen.user.domain.User;

public interface EmailVerificationService {
    void sendVerification(User user, String token);

    void sendEmailVerificationEmail(String email, String verificationToken, String firstName);
}
