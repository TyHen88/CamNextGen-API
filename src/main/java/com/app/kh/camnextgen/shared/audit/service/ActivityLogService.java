package com.app.kh.camnextgen.shared.audit.service;

public interface ActivityLogService {
    void logEvent(String event, Long actorUserId, String detailsJson);
}
