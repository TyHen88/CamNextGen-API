package com.app.kh.camnextgen.shared.audit.repo;

import com.app.kh.camnextgen.shared.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
