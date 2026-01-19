package com.app.kh.camnextgen.modules.enrollment.controller;

import com.app.kh.camnextgen.modules.enrollment.dto.CreateEnrollmentRequest;
import com.app.kh.camnextgen.modules.enrollment.dto.EnrollmentResponse;
import com.app.kh.camnextgen.modules.enrollment.service.EnrollmentService;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import com.app.kh.camnextgen.shared.response.ApiResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import com.app.kh.camnextgen.shared.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ENROLLMENT_WRITE')")
    public ApiResponse<EnrollmentResponse> enroll(@Valid @RequestBody CreateEnrollmentRequest request,
                                                  HttpServletRequest http) {
        Long userId = requireUserId();
        return ApiResponse.ok(enrollmentService.enroll(userId, request), requestId(http));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ENROLLMENT_READ')")
    public ApiResponse<PageResponse<EnrollmentResponse>> list(Pageable pageable, HttpServletRequest http) {
        Long userId = requireUserId();
        return ApiResponse.ok(enrollmentService.listForUser(userId, pageable), requestId(http));
    }

    private Long requireUserId() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("UNAUTHORIZED", "Unauthenticated");
        }
        return userId;
    }

    private String requestId(HttpServletRequest request) {
        Object value = request.getAttribute("correlationId");
        return value == null ? null : value.toString();
    }
}
