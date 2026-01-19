package com.app.kh.camnextgen.modules.audit.service;

import com.app.kh.camnextgen.modules.audit.domain.ActivityLog;
import com.app.kh.camnextgen.modules.audit.dto.ActivityLogResponse;
import com.app.kh.camnextgen.modules.audit.repository.ActivityLogRepository;
import com.app.kh.camnextgen.shared.response.PageResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("modulesActivityLogService")
public class ActivityLogServiceImpl implements ActivityLogService {
    private final ActivityLogRepository repository;

    public ActivityLogServiceImpl(ActivityLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void logEvent(String event, Long actorUserId, String detailsJson) {
        ActivityLog log = new ActivityLog();
        log.setEvent(event);
        log.setActorUserId(actorUserId == null ? 0L : actorUserId);
        log.setCreatedAt(Instant.now());
        log.setDetailsJson(detailsJson);
        repository.save(log);
    }

    @Override
    public PageResponse<ActivityLogResponse> list(Pageable pageable) {
        Page<ActivityLog> page = repository.findAll(pageable);
        List<ActivityLogResponse> items = page.getContent().stream()
            .map(this::toResponse)
            .toList();
        return new PageResponse<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private ActivityLogResponse toResponse(ActivityLog log) {
        return new ActivityLogResponse(log.getId(), log.getEvent(), log.getActorUserId(), log.getCreatedAt(), log.getDetailsJson());
    }
}
