package com.app.kh.camnextgen.shared.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.auth")
public class AuthProperties {
    private Duration verificationTokenTtl;
    private Duration passwordResetTokenTtl;

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
}
