package com.app.kh.camnextgen.modules.audit.dto;

import java.time.Instant;

public record AuditLogResponse(Long id, String event, Long actorUserId, Instant createdAt, String detailsJson) {}
