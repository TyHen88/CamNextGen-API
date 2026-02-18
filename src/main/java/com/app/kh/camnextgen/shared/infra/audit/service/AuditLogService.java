package com.app.kh.camnextgen.shared.infra.audit.service;

import com.app.kh.camnextgen.shared.infra.audit.domain.AuditLog;
import com.app.kh.camnextgen.shared.infra.audit.repository.AuditLogRepository;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(String event, Long actorUserId, String detailsJson) {
        AuditLog log = new AuditLog();
        log.setEvent(event);
        log.setActorUserId(actorUserId != null ? actorUserId : 0L);
        log.setCreatedAt(Instant.now());
        log.setDetailsJson(detailsJson);
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> list(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}
