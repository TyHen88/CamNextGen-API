package com.app.kh.camnextgen.shared.infra.audit.repository;

import com.app.kh.camnextgen.shared.infra.audit.domain.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
