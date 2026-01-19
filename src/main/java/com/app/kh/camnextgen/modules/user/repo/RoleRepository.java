package com.app.kh.camnextgen.modules.user.repo;

import com.app.kh.camnextgen.modules.user.domain.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code);
}
