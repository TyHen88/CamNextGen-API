package com.app.kh.camnextgen.modules.course.dto;

import com.app.kh.camnextgen.modules.course.domain.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCourseRequest(
    @NotBlank @Size(max = 50) String code,
    @NotBlank @Size(max = 200) String title,
    @Size(max = 4000) String description,
    CourseStatus status
) {}
