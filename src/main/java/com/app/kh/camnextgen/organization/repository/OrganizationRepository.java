package com.app.kh.camnextgen.organization.repository;

import com.app.kh.camnextgen.organization.domain.Organization;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByCode(String code);

    Optional<Organization> findByNameIgnoreCase(String name);

    boolean existsByCode(String code);
}
