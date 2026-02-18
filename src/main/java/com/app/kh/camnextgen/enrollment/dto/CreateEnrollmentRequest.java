package com.app.kh.camnextgen.enrollment.dto;

import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(@NotNull Long courseId) {}
