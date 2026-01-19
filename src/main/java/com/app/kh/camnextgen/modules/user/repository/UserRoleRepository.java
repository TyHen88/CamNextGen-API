package com.app.kh.camnextgen.modules.user.repository;

import com.app.kh.camnextgen.modules.user.domain.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
}
