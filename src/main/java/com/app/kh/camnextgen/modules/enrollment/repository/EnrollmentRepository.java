package com.app.kh.camnextgen.modules.enrollment.repository;

import com.app.kh.camnextgen.modules.enrollment.domain.Enrollment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    Page<Enrollment> findByUserId(Long userId, Pageable pageable);
}
