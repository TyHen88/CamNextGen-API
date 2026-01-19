package com.app.kh.camnextgen.modules.course.dto;

import com.app.kh.camnextgen.modules.course.domain.CourseStatus;

public record CourseResponse(Long id, String code, String title, String description, CourseStatus status) {}
