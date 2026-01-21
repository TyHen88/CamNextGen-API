package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private static final Logger log = LoggerFactory.getLogger(EmailVerificationServiceImpl.class);

    @Autowired
    private EmailService emailService;

    @Value("${camgennext.email.base-url:http://localhost:3000}")
    private String baseUrl;

    @Override
    public void sendVerification(User user, String token) {
        sendEmailVerificationEmail(user.getEmail(), token, user.getFullName());
    }

    @Override
    public void sendEmailVerificationEmail(String email, String verificationToken, String firstName) {
        try {
            emailService.sendEmailVerificationEmail(email, verificationToken, firstName);
            log.info("Email verification email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", email, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
