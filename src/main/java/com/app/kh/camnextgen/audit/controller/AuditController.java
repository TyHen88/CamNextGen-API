package com.app.kh.camnextgen.audit.controller;

import com.app.kh.camnextgen.audit.dto.ActivityLogResponse;
import com.app.kh.camnextgen.audit.dto.AuditLogResponse;
import com.app.kh.camnextgen.shared.infra.audit.service.ActivityLogService;
import com.app.kh.camnextgen.shared.infra.audit.service.AuditLogService;
import com.app.kh.camnextgen.shared.response.ApiResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AuditController {
    private final AuditLogService auditLogService;
    private final ActivityLogService activityLogService;

    public AuditController(AuditLogService auditLogService, ActivityLogService activityLogService) {
        this.auditLogService = auditLogService;
        this.activityLogService = activityLogService;
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ApiResponse<PageResponse<AuditLogResponse>> auditLogs(Pageable pageable, HttpServletRequest http) {
        Page<AuditLogResponse> page = auditLogService.list(pageable)
            .map(log -> new AuditLogResponse(log.getId(), log.getEvent(), log.getActorUserId(), log.getCreatedAt(), log.getDetailsJson()));
        
        PageResponse<AuditLogResponse> response = new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
        return ApiResponse.ok(response, requestId(http));
    }

    @GetMapping("/activity")
    @PreAuthorize("hasAuthority('ACTIVITY_READ')")
    public ApiResponse<PageResponse<ActivityLogResponse>> activityLogs(Pageable pageable, HttpServletRequest http) {
        Page<ActivityLogResponse> page = activityLogService.list(pageable)
            .map(log -> new ActivityLogResponse(log.getId(), log.getEvent(), log.getActorUserId(), log.getCreatedAt(), log.getDetailsJson()));

        PageResponse<ActivityLogResponse> response = new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
        return ApiResponse.ok(response, requestId(http));
    }

    private String requestId(HttpServletRequest request) {
        Object value = request.getAttribute("correlationId");
        return value == null ? null : value.toString();
    }
}
