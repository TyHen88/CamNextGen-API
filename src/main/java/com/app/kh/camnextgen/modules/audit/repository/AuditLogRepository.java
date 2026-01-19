package com.app.kh.camnextgen.modules.audit.repository;

import com.app.kh.camnextgen.modules.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
