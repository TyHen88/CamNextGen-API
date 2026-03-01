package com.app.kh.camnextgen.auth.service;

import com.app.kh.camnextgen.auth.domain.EmailOtp;
import com.app.kh.camnextgen.auth.domain.OtpPurpose;
import com.app.kh.camnextgen.auth.repository.EmailOtpRepository;
import com.app.kh.camnextgen.shared.config.AuthProperties;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import com.app.kh.camnextgen.user.domain.User;
import com.app.kh.camnextgen.user.domain.UserStatus;
import com.app.kh.camnextgen.user.repository.UserRepository;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final EmailOtpRepository emailOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthProperties authProperties;
    private final UserRepository userRepository;



    @Override
    @Transactional
    public void sendOtp(String email, OtpPurpose purpose) {
        String normalizedEmail = normalizeEmail(email);
        Instant now = Instant.now();

        emailOtpRepository.findTopByEmailAndPurposeAndUsedAtIsNullOrderByCreatedAtDesc(normalizedEmail, purpose)
            .ifPresent(existing -> {
                Instant cooldownUntil = existing.getCreatedAt().plus(authProperties.getOtpResendCooldown());
                if (cooldownUntil.isAfter(now)) {
                    throw new BusinessException("OTP_COOLDOWN", "Please wait before requesting another OTP");
                }
            });

        emailOtpRepository.invalidateActiveByEmailAndPurpose(normalizedEmail, purpose, now);

        String otp = generateOtp();
        EmailOtp entity = new EmailOtp();
        entity.setEmail(normalizedEmail);
        entity.setPurpose(purpose);
        entity.setOtpHash(passwordEncoder.encode(otp));
        entity.setCreatedAt(now);
        entity.setExpiresAt(now.plus(authProperties.getOtpTtl()));
        entity.setAttemptCount(0);
        emailOtpRepository.save(entity);

        sendOtpEmail(normalizedEmail, otp, purpose);
        log.info("OTP sent for email={} purpose={}", normalizedEmail, purpose);
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String otp, OtpPurpose purpose) {
        String normalizedEmail = normalizeEmail(email);
        Instant now = Instant.now();

        EmailOtp entity = emailOtpRepository.findTopByEmailAndPurposeAndUsedAtIsNullOrderByCreatedAtDesc(normalizedEmail, purpose)
            .orElseThrow(() -> new BusinessException("INVALID_OTP", "Invalid OTP"));

        if (entity.isExpired(now)) {
            throw new BusinessException("OTP_EXPIRED", "OTP has expired");
        }
        if (entity.getAttemptCount() >= authProperties.getOtpMaxAttempts()) {
            throw new BusinessException("OTP_ATTEMPTS_EXCEEDED", "OTP verification attempts exceeded");
        }
        if (!passwordEncoder.matches(otp, entity.getOtpHash())) {
            entity.incrementAttemptCount();
            emailOtpRepository.save(entity);
            throw new BusinessException("INVALID_OTP", "Invalid OTP");
        }

        entity.markUsed(now);
        emailOtpRepository.save(entity);

        if (purpose == OtpPurpose.EMAIL_VERIFICATION) {
            User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BusinessException("EMAIL_NOT_FOUND", "Email not found"));
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String generateOtp() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
    }

    private void sendOtpEmail(String email, String otp, OtpPurpose purpose) {
        String subject = "CamNextGen verification code";
        String purposeLabel = switch (purpose) {
            case EMAIL_VERIFICATION -> "email verification";
            case PASSWORD_RESET -> "password reset";
            case LOGIN_2FA -> "secure login";
        };
        long expiryMinutes = authProperties.getOtpTtl().toMinutes();

        String html = """
        <!doctype html>
        <html lang="en">
          <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>CamNextGen OTP</title>
          </head>
          <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,sans-serif;color:#1f2937;">
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="padding:24px 12px;">
              <tr>
                <td align="center">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width:560px;background:#ffffff;border-radius:14px;overflow:hidden;border:1px solid #e5e7eb;">
                    <tr>
                      <td style="background:#0f766e;padding:20px 24px;color:#ffffff;">
                        <h1 style="margin:0;font-size:20px;line-height:1.3;">CamNextGen</h1>
                        <p style="margin:6px 0 0;font-size:13px;opacity:.92;">Secure one-time verification code</p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:24px;">
                        <p style="margin:0 0 12px;font-size:15px;">You requested an OTP for <strong>%s</strong>.</p>
                        <p style="margin:0 0 8px;font-size:14px;color:#4b5563;">Enter this code:</p>

                        <div style="margin:12px 0 18px;padding:14px 18px;background:#f0fdfa;border:1px dashed #14b8a6;border-radius:10px;text-align:center;">
                          <span style="font-size:30px;letter-spacing:8px;font-weight:700;color:#0f766e;">%s</span>
                        </div>

                        <p style="margin:0 0 8px;font-size:14px;">This code expires in <strong>%d minutes</strong>.</p>
                        <p style="margin:0 0 16px;font-size:13px;color:#6b7280;">Do not share this code with anyone.</p>

                        <hr style="border:none;border-top:1px solid #e5e7eb;margin:16px 0;" />
                        <p style="margin:0;font-size:12px;color:#6b7280;">
                          If you didn’t request this, you can safely ignore this email.
                        </p>
                      </td>
                    </tr>
                  </table>

                  <p style="margin:14px 0 0;font-size:12px;color:#9ca3af;">
                    © CamNextGen
                  </p>
                </td>
              </tr>
            </table>
          </body>
        </html>
        """.formatted(purposeLabel, otp, expiryMinutes);

        try {
            emailService.sendEmail(email, subject, html);
        } catch (Exception e) {
            throw new BusinessException("EMAIL_SEND_FAILED", "Failed to send OTP email");
        }
    }
}
