package com.app.kh.camnextgen.modules.course.service;

import com.app.kh.camnextgen.modules.course.domain.Course;
import com.app.kh.camnextgen.modules.course.domain.CourseStatus;
import com.app.kh.camnextgen.modules.course.dto.CourseResponse;
import com.app.kh.camnextgen.modules.course.dto.CourseSummaryResponse;
import com.app.kh.camnextgen.modules.course.dto.CreateCourseRequest;
import com.app.kh.camnextgen.modules.course.dto.UpdateCourseRequest;
import com.app.kh.camnextgen.modules.course.repository.CourseRepository;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import com.app.kh.camnextgen.shared.exception.NotFoundException;
import com.app.kh.camnextgen.shared.response.PageResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public CourseResponse create(CreateCourseRequest request) {
        if (courseRepository.findByCode(request.code()).isPresent()) {
            throw new BusinessException("COURSE_EXISTS", "Course code already exists");
        }
        Course course = new Course();
        course.setCode(request.code());
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setStatus(request.status() == null ? CourseStatus.DRAFT : request.status());
        courseRepository.save(course);
        return toResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, UpdateCourseRequest request) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("COURSE_NOT_FOUND", "Course not found"));
        if (request.title() != null) {
            course.setTitle(request.title());
        }
        if (request.description() != null) {
            course.setDescription(request.description());
        }
        if (request.status() != null) {
            course.setStatus(request.status());
        }
        return toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getById(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("COURSE_NOT_FOUND", "Course not found"));
        return toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseSummaryResponse> list(Pageable pageable) {
        Page<Course> page = courseRepository.findAll(pageable);
        List<CourseSummaryResponse> items = page.getContent().stream()
            .map(this::toSummary)
            .toList();
        return new PageResponse<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private CourseResponse toResponse(Course course) {
        return new CourseResponse(course.getId(), course.getCode(), course.getTitle(), course.getDescription(), course.getStatus());
    }

    private CourseSummaryResponse toSummary(Course course) {
        return new CourseSummaryResponse(course.getId(), course.getCode(), course.getTitle(), course.getStatus());
    }
}
