package com.app.kh.camnextgen.enrollment.dto;

import com.app.kh.camnextgen.enrollment.domain.EnrollmentStatus;
import java.time.Instant;

public record EnrollmentResponse(Long id, Long userId, Long courseId, EnrollmentStatus status, Instant enrolledAt) {}
