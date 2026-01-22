package com.app.kh.camnextgen.modules.auth.service;

import com.app.kh.camnextgen.modules.user.domain.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

// mail:
//   password: "N$rply!@"  # Quote to escape special characters

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private static final Logger log = LoggerFactory.getLogger(EmailVerificationServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${camgennext.email.from:noreply@wecambodia.com}")
    private String fromEmail;

    @Value("${camgennext.email.from-name:CamNextGen}")
    private String fromName;

    @Value("${camgennext.email.base-url:http://localhost:3006}")
    private String baseUrl;

    @Override
    public void sendVerification(User user, String token) {
        sendEmailVerificationEmail(user.getEmail(), token, user.getFullName());
    }

    @Override
    public void sendEmailVerificationEmail(String email, String verificationToken, String firstName) {
        try {
            String verificationUrl = baseUrl + "/verify?token=" + verificationToken;
            String subject = "Verify Your Email Address";
            String htmlContent = buildEmailVerificationHtml(verificationUrl, email, firstName);
            sendEmail(email, subject, htmlContent);
            log.info("Email verification email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", email, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set from address with proper InternetAddress handling
            helper.setFrom(new InternetAddress(fromEmail, fromName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content

            log.info("Sending email from: {} to: {} with subject: {}", fromEmail, to, subject);
            mailSender.send(message);
            log.info("Email sent successfully from: {} to: {}", fromEmail, to);
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to set email from address: {}", e.getMessage());
            throw new MessagingException("Failed to set email from address", e);
        }
    }

    private String buildEmailVerificationHtml(String verificationUrl, String email, String firstName) {
        String greeting = (firstName != null && !firstName.trim().isEmpty())
                ? "Hello " + firstName + ","
                : "Hello,";

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .button { display: inline-block; padding: 12px 24px; background-color: #3b82f6; color: #ffffff; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { margin-top: 30px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>Verify Your Email Address</h2>
                        <p>%s</p>
                        <p>Thank you for registering with CamNextGen. Please verify your email address by clicking the button below:</p>
                        <a href="%s" style="background-color: #3b82f6; color: #ffffff; text-decoration: none; border-radius: 5px; padding: 12px 24px; display: inline-block; margin: 20px 0; font-weight: 500; border: none;">Verify Email</a>
                        <p>Or copy and paste this link into your browser:</p>
                        <p>%s</p>
                        <p>This link will expire in 24 hours.</p>
                        <p>If you did not create an account, please ignore this email.</p>
                        <div class="footer">
                            <p>Best regards,<br>CamNextGen Team</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(greeting, verificationUrl, verificationUrl);
    }
}