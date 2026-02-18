package com.app.kh.camnextgen.shared.infra.audit.repository;

import com.app.kh.camnextgen.shared.infra.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
