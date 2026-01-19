package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.user.domain.User;

public interface EmailVerificationService {
    void createVerification(User user);

    void verifyToken(String token);
}
