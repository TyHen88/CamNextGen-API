package com.app.kh.camnextgen.modules.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderImpl implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSenderImpl.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("email to={} subject={} bodyLength={}", to, subject, body == null ? 0 : body.length());
    }
}
