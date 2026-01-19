package com.app.kh.camnextgen.shared.audit.service;

import com.app.kh.camnextgen.shared.audit.domain.AuditLog;
import com.app.kh.camnextgen.shared.audit.repo.AuditLogRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(String event, Long actorUserId, String detailsJson) {
        AuditLog log = new AuditLog();
        log.setEvent(event);
        log.setActorUserId(actorUserId);
        log.setCreatedAt(Instant.now());
        log.setDetailsJson(detailsJson);
        auditLogRepository.save(log);
    }
}
