package com.app.kh.camnextgen.shared.audit.service;

public interface AuditLogService {
    void logEvent(String event, Long actorUserId, String detailsJson);
}
