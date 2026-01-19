package com.app.kh.camnextgen.modules.course.repository;

import com.app.kh.camnextgen.modules.course.domain.Course;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
}
