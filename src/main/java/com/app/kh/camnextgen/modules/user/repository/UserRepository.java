package com.app.kh.camnextgen.modules.user.repository;

import com.app.kh.camnextgen.modules.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    @Query("select u from User u " +
           "left join fetch u.roles ur " +
           "left join fetch ur.role r " +
           "left join fetch r.permissions rp " +
           "left join fetch rp.permission p " +
           "where u.id = :id")
    Optional<User> findByIdWithAuthorities(@Param("id") Long id);
}
