package com.app.kh.camnextgen.auth.dto;

import com.app.kh.camnextgen.auth.domain.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpRequest(
    @Email @NotBlank String email,
    @NotBlank @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits") String otp,
    @NotNull OtpPurpose purpose
) {}
