package com.app.kh.camnextgen.modules.auth.service;

public interface EmailSender {
    void sendEmail(String to, String subject, String body);
}
