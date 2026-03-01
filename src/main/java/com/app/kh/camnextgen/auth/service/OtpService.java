package com.app.kh.camnextgen.auth.service;

import com.app.kh.camnextgen.auth.domain.OtpPurpose;

public interface OtpService {
    void sendOtp(String email, OtpPurpose purpose);

    default void verifyOtp(String email, String otp, OtpPurpose purpose) {
        verifyOtp(email, otp, purpose, true);
    }

    void verifyOtp(String email, String otp, OtpPurpose purpose, boolean consumeOnSuccess);
}
