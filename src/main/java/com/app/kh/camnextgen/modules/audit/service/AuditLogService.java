package com.app.kh.camnextgen.modules.audit.service;

import com.app.kh.camnextgen.modules.audit.dto.AuditLogResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void logEvent(String event, Long actorUserId, String detailsJson);
    PageResponse<AuditLogResponse> list(Pageable pageable);
}
