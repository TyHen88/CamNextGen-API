package com.app.kh.camnextgen.user.repository;

import com.app.kh.camnextgen.user.domain.Permission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);
}
