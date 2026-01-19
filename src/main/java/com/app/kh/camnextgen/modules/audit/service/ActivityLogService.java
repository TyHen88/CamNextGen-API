package com.app.kh.camnextgen.modules.audit.service;

import com.app.kh.camnextgen.modules.audit.dto.ActivityLogResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ActivityLogService {
    void logEvent(String event, Long actorUserId, String detailsJson);
    PageResponse<ActivityLogResponse> list(Pageable pageable);
}
