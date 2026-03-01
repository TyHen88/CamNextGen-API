package com.app.kh.camnextgen.shared.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.auth")
public class AuthProperties {
    private Duration verificationTokenTtl;
    private Duration passwordResetTokenTtl;
    private Duration otpTtl = Duration.ofMinutes(5);
    private Duration otpResendCooldown = Duration.ofMinutes(1);
    private int otpMaxAttempts = 5;

    public Duration getVerificationTokenTtl() {
        return verificationTokenTtl;
    }

    public void setVerificationTokenTtl(Duration verificationTokenTtl) {
        this.verificationTokenTtl = verificationTokenTtl;
    }

    public Duration getPasswordResetTokenTtl() {
        return passwordResetTokenTtl;
    }

    public void setPasswordResetTokenTtl(Duration passwordResetTokenTtl) {
        this.passwordResetTokenTtl = passwordResetTokenTtl;
    }

    public Duration getOtpTtl() {
        return otpTtl;
    }

    public void setOtpTtl(Duration otpTtl) {
        this.otpTtl = otpTtl;
    }

    public Duration getOtpResendCooldown() {
        return otpResendCooldown;
    }

    public void setOtpResendCooldown(Duration otpResendCooldown) {
        this.otpResendCooldown = otpResendCooldown;
    }

    public int getOtpMaxAttempts() {
        return otpMaxAttempts;
    }

    public void setOtpMaxAttempts(int otpMaxAttempts) {
        this.otpMaxAttempts = otpMaxAttempts;
    }
}
