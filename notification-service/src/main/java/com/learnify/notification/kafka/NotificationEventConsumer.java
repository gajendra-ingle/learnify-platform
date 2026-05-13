package com.learnify.notification.kafka;

import com.learnify.notification.service.impl.EmailNotificationServiceImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification Kafka Consumer.
 *
 * Listens to multiple topics and sends appropriate notifications:
 * - enrollment-confirmed: sends enrollment confirmation email
 * - payment-success: sends payment receipt email
 * - enrollment-completed: sends course completion certificate
 * - user-registered: sends welcome email
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final EmailNotificationServiceImpl emailService;

    @KafkaListener(
            topics = "enrollment-confirmed",
            groupId = "notification-service-group"
    )
    public void handleEnrollmentConfirmed(@Payload EnrollmentConfirmedEvent event, Acknowledgment acknowledgment) {
        log.info("Processing enrollment-confirmed notification: studentId={}, courseId={}", event.getStudentId(), event.getCourseId());
        try {
            emailService.sendEnrollmentConfirmation(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to send enrollment confirmation: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "payment-success",
            groupId = "notification-service-group"
    )
    public void handlePaymentSuccess(@Payload PaymentSuccessEvent event, Acknowledgment acknowledgment) {
        log.info("Processing payment-success notification: paymentId={}", event.getPaymentId());
        try {
            emailService.sendPaymentReceipt(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to send payment receipt: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "enrollment-completed",
            groupId = "notification-service-group"
    )
    public void handleEnrollmentCompleted(@Payload EnrollmentCompletedEvent event, Acknowledgment acknowledgment) {
        log.info("Processing enrollment-completed notification: enrollmentId={}", event.getEnrollmentId());
        try {
            emailService.sendCourseCompletionCertificate(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to send completion certificate: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "user-registered",
            groupId = "notification-service-group"
    )
    public void handleUserRegistered(@Payload UserRegisteredEvent event, Acknowledgment acknowledgment) {
        log.info("Processing user-registered notification: userId={}", event.getUserId());
        try {
            emailService.sendWelcomeEmail(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage(), e);
        }
    }

    // Event POJOs
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentConfirmedEvent {
        private UUID enrollmentId;
        private UUID studentId;
        private UUID courseId;
        private String courseTitle;
        private String amountPaid;
        private LocalDateTime enrolledAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentSuccessEvent {
        private UUID paymentId;
        private UUID studentId;
        private UUID courseId;
        private String courseTitle;
        private BigDecimal amountPaid;
        private String currency;
        private String transactionRef;
        private LocalDateTime paidAt;
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRegisteredEvent {
        private String userId;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        private LocalDateTime registeredAt;
    }

}
