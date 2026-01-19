package com.app.kh.camnextgen.shared.audit.repo;

import com.app.kh.camnextgen.shared.audit.domain.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
