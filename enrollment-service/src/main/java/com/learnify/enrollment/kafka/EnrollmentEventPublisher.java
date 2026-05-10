package com.learnify.enrollment.kafka;


import com.learnify.enrollment.entity.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnrollmentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public static final String ENROLLMENT_CONFIRMED_TOPIC = "enrollment-confirmed";
    public static final String ENROLLMENT_COMPLETED_TOPIC = "enrollment-completed";

    public void publishEnrollmentConfirmed(Enrollment enrollment) {
        EnrollmentConfirmedEvent event = EnrollmentConfirmedEvent.builder()
                .enrollmentId(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .courseTitle(enrollment.getCourseTitle())
                .instructorId(enrollment.getInstructorId())
                .amountPaid(enrollment.getAmountPaid() != null ? enrollment.getAmountPaid().toPlainString() : "0")
                .enrolledAt(enrollment.getEnrolledAt())
                .build();

        kafkaTemplate.send(ENROLLMENT_CONFIRMED_TOPIC, enrollment.getStudentId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Published enrollment-confirmed event: enrollmentId={}", enrollment.getId());
                    } else {
                        log.error("Failed to publish enrollment-confirmed event: {}", ex.getMessage());
                    }
                });
    }

    public void publishEnrollmentCompleted(Enrollment enrollment) {
        EnrollmentCompletedEvent event = EnrollmentCompletedEvent.builder()
                .enrollmentId(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .courseTitle(enrollment.getCourseTitle())
                .completedAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send(ENROLLMENT_COMPLETED_TOPIC, enrollment.getStudentId().toString(), event);
        log.info("Published enrollment-completed event: enrollmentId={}", enrollment.getId());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentConfirmedEvent {
        private UUID enrollmentId;
        private UUID studentId;
        private UUID courseId;
        private String courseTitle;
        private UUID instructorId;
        private String amountPaid;
        private LocalDateTime enrolledAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentCompletedEvent {
        private UUID enrollmentId;
        private UUID studentId;
        private UUID courseId;
        private String courseTitle;
        private LocalDateTime completedAt;
    }
}

