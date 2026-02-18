package com.app.kh.camnextgen.auth.service;

public interface EmailSender {
    void sendEmail(String to, String subject, String body);
}
