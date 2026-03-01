-- email_otps: one-time password records for email verification and auth challenges
CREATE TABLE IF NOT EXISTS email_otps (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    purpose VARCHAR(40) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    attempt_count INT NOT NULL DEFAULT 0,
    CONSTRAINT chk_email_otps_attempt_count_non_negative CHECK (attempt_count >= 0)
);

CREATE INDEX IF NOT EXISTS idx_email_otp_email ON email_otps(email);
CREATE INDEX IF NOT EXISTS idx_email_otp_purpose ON email_otps(purpose);
CREATE INDEX IF NOT EXISTS idx_email_otp_expires_at ON email_otps(expires_at);
