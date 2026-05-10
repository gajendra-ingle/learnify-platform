package com.learnify.enrollment.service.Impl;

import com.learnify.enrollment.service.EnrollmentService;
import com.learnify.enrollment.client.CourseServiceClient;
import com.learnify.enrollment.dto.request.EnrollmentRequest;
import com.learnify.enrollment.dto.response.CourseResponse;
import com.learnify.enrollment.dto.response.EnrollmentResponse;
import com.learnify.enrollment.entity.Enrollment;
import com.learnify.enrollment.entity.EnrollmentStatus;
import com.learnify.enrollment.event.EnrollmentCreatedEvent;
import com.learnify.enrollment.event.EnrollmentEventProducer;
import com.learnify.enrollment.exception.EnrollmentException;
import com.learnify.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseServiceClient courseServiceClient;
    private final EnrollmentEventProducer eventProducer;

    @Transactional
    public EnrollmentResponse enrollStudent(UUID studentId, EnrollmentRequest request) {
        UUID courseId = request.getCourseId();

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new EnrollmentException("Student already enrolled in this course");
        }

        // Feign call to course service with circuit breaker
        CourseResponse course = courseServiceClient.getCourseById(courseId);
        if (course == null) {
            throw new EnrollmentException("Course not found or service unavailable");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercentage(0.0)
                .build();

        enrollment = enrollmentRepository.save(enrollment);

        // Publish event to Kafka
        eventProducer.publishEnrollmentCreated(EnrollmentCreatedEvent.builder()
                .enrollmentId(enrollment.getId())
                .studentId(studentId)
                .courseId(courseId)
                .courseTitle(course.getTitle())
                .enrolledAt(enrollment.getEnrolledAt())
                .build());

        log.info("Student {} enrolled in course {}", studentId, courseId);
        return mapToResponse(enrollment, course.getTitle());
    }

    @Transactional
    public void enrollStudentAfterPayment(UUID studentId, UUID courseId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            log.warn("Student {} already enrolled in course {}", studentId, courseId);
            return;
        }

        CourseResponse course = courseServiceClient.getCourseById(courseId);
        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercentage(0.0)
                .build();

        enrollmentRepository.save(enrollment);
        log.info("Auto-enrolled student {} in course {} after payment", studentId, courseId);
    }

    // to-do
    @Transactional
    @Override
    public void enrollAfterPayment(UUID studentId, UUID courseId, UUID paymentId, BigDecimal amountPaid) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            log.warn("Student {} already enrolled in course {}", studentId, courseId);
            return;
        }

        CourseResponse course = courseServiceClient.getCourseById(courseId);
        if (course == null) {
            throw new EnrollmentException("Course not found");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .courseTitle(course.getTitle())
                .instructorId(course.getInstructorId())
                .paymentId(paymentId)
                .amountPaid(amountPaid)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercentage(0.0)
                .build();

        enrollment = enrollmentRepository.save(enrollment);

        // publish kafka event
        eventProducer.publishEnrollmentCreated(
                EnrollmentCreatedEvent.builder()
                        .enrollmentId(enrollment.getId())
                        .studentId(studentId)
                        .courseId(courseId)
                        .courseTitle(course.getTitle())
                        .enrolledAt(enrollment.getEnrolledAt())
                        .build()
        );

        log.info("Student {} enrolled in course {} after successful payment {}", studentId, courseId, paymentId);
    }

    // to-do
    @Transactional
    @Override
    public void cancelEnrollmentByPayment(UUID paymentId) {
        Enrollment enrollment = enrollmentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new EnrollmentException("Enrollment not found for paymentId: " + paymentId));

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
        log.info("Enrollment cancelled successfully for paymentId={}, enrollmentId={}", paymentId, enrollment.getId());
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getStudentEnrollments(UUID studentId, Pageable pageable) {
        return enrollmentRepository.findByStudentId(studentId, pageable)
                .map(e -> mapToResponse(e, null));
    }

    @Transactional
    public EnrollmentResponse updateProgress(UUID studentId, UUID courseId, Double progress) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new EnrollmentException("Enrollment not found"));

        enrollment.setProgressPercentage(progress);
        enrollment.setLastAccessedAt(LocalDateTime.now());

        if (progress >= 100.0) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
        }

        return mapToResponse(enrollmentRepository.save(enrollment), null);
    }

    private EnrollmentResponse mapToResponse(Enrollment e, String courseTitle) {
        return EnrollmentResponse.builder()
                .id(e.getId())
                .studentId(e.getStudentId())
                .courseId(e.getCourseId())
                .courseTitle(courseTitle)
                .status(e.getStatus().name())
                .progressPercentage(e.getProgressPercentage())
                .enrolledAt(e.getEnrolledAt())
                .completedAt(e.getCompletedAt())
                .lastAccessedAt(e.getLastAccessedAt())
                .build();
    }
}
