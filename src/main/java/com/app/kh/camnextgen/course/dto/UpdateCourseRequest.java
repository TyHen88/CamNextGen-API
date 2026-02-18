package com.app.kh.camnextgen.course.dto;

import com.app.kh.camnextgen.course.domain.CourseStatus;
import jakarta.validation.constraints.Size;

public record UpdateCourseRequest(
    @Size(max = 200) String title,
    @Size(max = 4000) String description,
    CourseStatus status
) {}
