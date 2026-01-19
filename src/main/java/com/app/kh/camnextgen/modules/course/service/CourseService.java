package com.app.kh.camnextgen.modules.course.service;

import com.app.kh.camnextgen.modules.course.dto.CourseResponse;
import com.app.kh.camnextgen.modules.course.dto.CourseSummaryResponse;
import com.app.kh.camnextgen.modules.course.dto.CreateCourseRequest;
import com.app.kh.camnextgen.modules.course.dto.UpdateCourseRequest;
import com.app.kh.camnextgen.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    CourseResponse create(CreateCourseRequest request);
    CourseResponse update(Long id, UpdateCourseRequest request);
    CourseResponse getById(Long id);
    PageResponse<CourseSummaryResponse> list(Pageable pageable);
}
