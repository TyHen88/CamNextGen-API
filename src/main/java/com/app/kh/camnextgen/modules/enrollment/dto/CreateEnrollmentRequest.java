package com.app.kh.camnextgen.modules.enrollment.dto;

import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(@NotNull Long courseId) {}
