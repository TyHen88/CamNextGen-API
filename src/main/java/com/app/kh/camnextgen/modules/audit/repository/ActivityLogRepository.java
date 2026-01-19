package com.app.kh.camnextgen.modules.audit.repository;

import com.app.kh.camnextgen.modules.audit.domain.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
