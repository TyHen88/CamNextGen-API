package com.app.kh.camnextgen.modules.user.repo;

import com.app.kh.camnextgen.modules.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role", "userRoles.role.rolePermissions", "userRoles.role.rolePermissions.permission"})
    Optional<User> findByEmailWithRoles(String email);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role", "userRoles.role.rolePermissions", "userRoles.role.rolePermissions.permission"})
    Optional<User> findByIdWithRoles(Long id);
}
