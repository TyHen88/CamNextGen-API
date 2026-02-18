package com.app.kh.camnextgen.course.dto;

import com.app.kh.camnextgen.course.domain.CourseStatus;

public record CourseSummaryResponse(Long id, String code, String title, CourseStatus status) {}
