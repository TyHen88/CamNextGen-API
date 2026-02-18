package com.app.kh.camnextgen.shared.infra.audit.service;

import com.app.kh.camnextgen.shared.infra.audit.domain.ActivityLog;
import com.app.kh.camnextgen.shared.infra.audit.repository.ActivityLogRepository;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(String event, Long actorUserId, String detailsJson) {
        ActivityLog log = new ActivityLog();
        log.setEvent(event);
        log.setActorUserId(actorUserId != null ? actorUserId : 0L);
        log.setCreatedAt(Instant.now());
        log.setDetailsJson(detailsJson);
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLog> list(Pageable pageable) {
        return activityLogRepository.findAll(pageable);
    }
}
