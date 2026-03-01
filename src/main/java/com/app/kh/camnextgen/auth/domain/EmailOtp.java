package com.app.kh.camnextgen.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "email_otps", indexes = {
    @Index(name = "idx_email_otp_email", columnList = "email"),
    @Index(name = "idx_email_otp_expires_at", columnList = "expires_at"),
    @Index(name = "idx_email_otp_purpose", columnList = "purpose")
})
@Getter
@Setter
public class EmailOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OtpPurpose purpose;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isExpired(Instant now) {
        return expiresAt == null || !expiresAt.isAfter(now);
    }

    public void markUsed(Instant usedAt) {
        this.usedAt = usedAt;
    }

    public void incrementAttemptCount() {
        this.attemptCount += 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailOtp emailOtp = (EmailOtp) o;
        return id != null && Objects.equals(id, emailOtp.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
