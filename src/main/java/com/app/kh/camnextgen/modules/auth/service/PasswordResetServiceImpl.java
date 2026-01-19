package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    @Override
    public void sendReset(User user, String token) {
        log.info("Send password reset to {} with token {}", user.getEmail(), token);
    }
}
