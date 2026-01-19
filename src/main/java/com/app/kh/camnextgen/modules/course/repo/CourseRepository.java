package com.app.kh.camnextgen.modules.course.repo;

import com.app.kh.camnextgen.modules.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
