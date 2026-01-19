package com.app.kh.camnextgen.modules.course.controller;

import com.app.kh.camnextgen.modules.course.dto.CourseResponse;
import com.app.kh.camnextgen.modules.course.dto.CourseSummaryResponse;
import com.app.kh.camnextgen.modules.course.dto.CreateCourseRequest;
import com.app.kh.camnextgen.modules.course.dto.UpdateCourseRequest;
import com.app.kh.camnextgen.modules.course.service.CourseService;
import com.app.kh.camnextgen.shared.response.ApiResponse;
import com.app.kh.camnextgen.shared.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COURSE_WRITE')")
    public ApiResponse<CourseResponse> create(@Valid @RequestBody CreateCourseRequest request, HttpServletRequest http) {
        return ApiResponse.ok(courseService.create(request), requestId(http));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_WRITE')")
    public ApiResponse<CourseResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody UpdateCourseRequest request,
                                              HttpServletRequest http) {
        return ApiResponse.ok(courseService.update(id, request), requestId(http));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_READ')")
    public ApiResponse<CourseResponse> get(@PathVariable Long id, HttpServletRequest http) {
        return ApiResponse.ok(courseService.getById(id), requestId(http));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COURSE_READ')")
    public ApiResponse<PageResponse<CourseSummaryResponse>> list(Pageable pageable, HttpServletRequest http) {
        return ApiResponse.ok(courseService.list(pageable), requestId(http));
    }

    private String requestId(HttpServletRequest request) {
        Object value = request.getAttribute("correlationId");
        return value == null ? null : value.toString();
    }
}
