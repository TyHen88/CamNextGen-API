package com.app.kh.camnextgen.modules.user.repo;

import com.app.kh.camnextgen.modules.user.domain.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
}
