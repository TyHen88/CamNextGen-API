package com.app.kh.camnextgen.modules.enrollment.service;

import com.app.kh.camnextgen.modules.course.domain.Course;
import com.app.kh.camnextgen.modules.course.repository.CourseRepository;
import com.app.kh.camnextgen.modules.enrollment.domain.Enrollment;
import com.app.kh.camnextgen.modules.enrollment.domain.EnrollmentStatus;
import com.app.kh.camnextgen.modules.enrollment.dto.CreateEnrollmentRequest;
import com.app.kh.camnextgen.modules.enrollment.dto.EnrollmentResponse;
import com.app.kh.camnextgen.modules.enrollment.repository.EnrollmentRepository;
import com.app.kh.camnextgen.modules.user.domain.User;
import com.app.kh.camnextgen.modules.user.repository.UserRepository;
import com.app.kh.camnextgen.shared.exception.BusinessException;
import com.app.kh.camnextgen.shared.exception.NotFoundException;
import com.app.kh.camnextgen.shared.response.PageResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 UserRepository userRepository,
                                 CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public EnrollmentResponse enroll(Long userId, CreateEnrollmentRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found"));
        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new NotFoundException("COURSE_NOT_FOUND", "Course not found"));
        if (enrollmentRepository.findByUserIdAndCourseId(userId, course.getId()).isPresent()) {
            throw new BusinessException("ALREADY_ENROLLED", "User already enrolled");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setEnrolledAt(Instant.now());
        enrollmentRepository.save(enrollment);
        return toResponse(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EnrollmentResponse> listForUser(Long userId, Pageable pageable) {
        Page<Enrollment> page = enrollmentRepository.findByUserId(userId, pageable);
        List<EnrollmentResponse> items = page.getContent().stream()
            .map(this::toResponse)
            .toList();
        return new PageResponse<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
            enrollment.getId(),
            enrollment.getUser().getId(),
            enrollment.getCourse().getId(),
            enrollment.getStatus(),
            enrollment.getEnrolledAt()
        );
    }
}
