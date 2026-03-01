package com.app.kh.camnextgen.auth.service;

import com.app.kh.camnextgen.auth.domain.OtpPurpose;

public interface OtpService {
    void sendOtp(String email, OtpPurpose purpose);
    void verifyOtp(String email, String otp, OtpPurpose purpose);
}
