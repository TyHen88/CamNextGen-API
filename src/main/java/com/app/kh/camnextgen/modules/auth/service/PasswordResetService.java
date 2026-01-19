package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.user.domain.User;

public interface PasswordResetService {
    void sendReset(User user, String token);
}
