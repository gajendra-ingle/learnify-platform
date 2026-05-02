package com.learnify.analytics.event;

import com.learnify.analytics.entity.CourseAnalytics;
import com.learnify.analytics.repository.CourseAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventConsumer {

    private final CourseAnalyticsRepository analyticsRepository;

    @KafkaListener(
            topics = "${kafka.topics.enrollment-created:enrollment-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handleEnrollment(EnrollmentCreatedEvent event) {
        log.info("Analytics: Processing enrollment for course: {}", event.getCourseId());
        CourseAnalytics analytics = analyticsRepository.findByCourseId(event.getCourseId())
                .orElse(CourseAnalytics.builder().courseId(event.getCourseId()).build());

        analytics.setTotalEnrollments(analytics.getTotalEnrollments() + 1);
        analytics.setActiveStudents(analytics.getActiveStudents() + 1);
        analytics.setLastUpdated(LocalDateTime.now());
        analyticsRepository.save(analytics);
    }

    @KafkaListener(
            topics = "${kafka.topics.payment-success:payment-success}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handlePayment(PaymentSuccessEvent event) {
        log.info("Analytics: Processing payment for course: {}", event.getCourseId());
        CourseAnalytics analytics = analyticsRepository.findByCourseId(event.getCourseId())
                .orElse(CourseAnalytics.builder().courseId(event.getCourseId()).build());

        analytics.setTotalRevenue(analytics.getTotalRevenue().add(event.getAmount()));
        analytics.setLastUpdated(LocalDateTime.now());
        analyticsRepository.save(analytics);
    }

}
