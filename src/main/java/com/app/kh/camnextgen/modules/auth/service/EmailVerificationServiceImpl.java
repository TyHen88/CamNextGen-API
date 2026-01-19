package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private static final Logger log = LoggerFactory.getLogger(EmailVerificationServiceImpl.class);

    @Override
    public void sendVerification(User user, String token) {
        log.info("Send verification to {} with token {}", user.getEmail(), token);
    }
}
