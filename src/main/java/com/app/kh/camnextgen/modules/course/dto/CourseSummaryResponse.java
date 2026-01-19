package com.app.kh.camnextgen.modules.course.dto;

import com.app.kh.camnextgen.modules.course.domain.CourseStatus;

public record CourseSummaryResponse(Long id, String code, String title, CourseStatus status) {}
