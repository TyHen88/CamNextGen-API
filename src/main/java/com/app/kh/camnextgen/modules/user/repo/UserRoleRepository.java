package com.app.kh.camnextgen.modules.user.repo;

import com.app.kh.camnextgen.modules.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
