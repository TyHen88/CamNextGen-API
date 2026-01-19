package com.app.kh.camnextgen.shared.audit.repository;

import com.app.kh.camnextgen.shared.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("sharedAuditLogRepository")
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
