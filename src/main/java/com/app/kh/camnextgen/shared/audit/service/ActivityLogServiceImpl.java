package com.app.kh.camnextgen.shared.audit.service;

import com.app.kh.camnextgen.shared.audit.domain.ActivityLog;
import com.app.kh.camnextgen.shared.audit.repository.ActivityLogRepository;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("sharedActivityLogService")
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogServiceImpl(@Qualifier("sharedActivityLogRepository") ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(String event, Long actorUserId, String detailsJson) {
        ActivityLog log = new ActivityLog();
        log.setEvent(event);
        log.setActorUserId(actorUserId);
        log.setCreatedAt(Instant.now());
        log.setDetailsJson(detailsJson);
        activityLogRepository.save(log);
    }
}
