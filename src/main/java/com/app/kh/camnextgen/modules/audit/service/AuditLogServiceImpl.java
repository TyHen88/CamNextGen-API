package com.app.kh.camnextgen.modules.audit.service;

import com.app.kh.camnextgen.modules.audit.domain.AuditLog;
import com.app.kh.camnextgen.modules.audit.dto.AuditLogResponse;
import com.app.kh.camnextgen.modules.audit.repository.AuditLogRepository;
import com.app.kh.camnextgen.shared.response.PageResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("modulesAuditLogService")
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository repository;

    public AuditLogServiceImpl(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void logEvent(String event, Long actorUserId, String detailsJson) {
        AuditLog log = new AuditLog();
        log.setEvent(event);
        log.setActorUserId(actorUserId == null ? 0L : actorUserId);
        log.setCreatedAt(Instant.now());
        log.setDetailsJson(detailsJson);
        repository.save(log);
    }

    @Override
    public PageResponse<AuditLogResponse> list(Pageable pageable) {
        Page<AuditLog> page = repository.findAll(pageable);
        List<AuditLogResponse> items = page.getContent().stream()
            .map(this::toResponse)
            .toList();
        return new PageResponse<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(log.getId(), log.getEvent(), log.getActorUserId(), log.getCreatedAt(), log.getDetailsJson());
    }
}
