package com.app.kh.camnextgen.modules.audit.controller;

import com.app.kh.camnextgen.modules.audit.dto.ActivityLogResponse;
import com.app.kh.camnextgen.modules.audit.dto.AuditLogResponse;
import com.app.kh.camnextgen.modules.audit.service.ActivityLogService;
import com.app.kh.camnextgen.modules.audit.service.AuditLogService;
import com.app.kh.camnextgen.shared.response.ApiResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuditController {
    private final AuditLogService auditLogService;
    private final ActivityLogService activityLogService;

    public AuditController(@Qualifier("modulesAuditLogService") AuditLogService auditLogService, @Qualifier("modulesActivityLogService") ActivityLogService activityLogService) {
        this.auditLogService = auditLogService;
        this.activityLogService = activityLogService;
    }

    @GetMapping("/audit")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ApiResponse<PageResponse<AuditLogResponse>> auditLogs(Pageable pageable, HttpServletRequest http) {
        return ApiResponse.ok(auditLogService.list(pageable), requestId(http));
    }

    @GetMapping("/activity")
    @PreAuthorize("hasAuthority('ACTIVITY_READ')")
    public ApiResponse<PageResponse<ActivityLogResponse>> activityLogs(Pageable pageable, HttpServletRequest http) {
        return ApiResponse.ok(activityLogService.list(pageable), requestId(http));
    }

    private String requestId(HttpServletRequest request) {
        Object value = request.getAttribute("correlationId");
        return value == null ? null : value.toString();
    }
}
