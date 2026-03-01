package com.app.kh.camnextgen.auth.dto;

import com.app.kh.camnextgen.auth.domain.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendOtpRequest(
    @Email @NotBlank String email,
    @NotNull OtpPurpose purpose
) {}
