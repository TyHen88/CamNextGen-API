package com.app.kh.camnextgen.modules.enrollment.repo;

import com.app.kh.camnextgen.modules.enrollment.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
