package com.app.kh.camnextgen.user.repository;

import com.app.kh.camnextgen.user.domain.RolePermission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Long roleId);
}
