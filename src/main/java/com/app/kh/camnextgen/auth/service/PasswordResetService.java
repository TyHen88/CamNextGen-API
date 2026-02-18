package com.app.kh.camnextgen.auth.service;

import com.app.kh.camnextgen.user.domain.User;

public interface PasswordResetService {
    void sendReset(User user, String token);
}
