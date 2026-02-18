package com.app.kh.camnextgen.course.dto;

import com.app.kh.camnextgen.course.domain.CourseStatus;

public record CourseResponse(Long id, String code, String title, String description, CourseStatus status) {}
