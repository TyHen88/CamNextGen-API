package com.app.kh.camnextgen.enrollment.service;

import com.app.kh.camnextgen.enrollment.dto.CreateEnrollmentRequest;
import com.app.kh.camnextgen.enrollment.dto.EnrollmentResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EnrollmentService {
    EnrollmentResponse enroll(Long userId, CreateEnrollmentRequest request);
    PageResponse<EnrollmentResponse> listForUser(Long userId, Pageable pageable);
}
