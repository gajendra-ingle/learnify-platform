package com.learnify.payment.kafka;

import com.learnify.payment.entity.Payment;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Payment Event Publisher.
 * <p>
 * Publishes critical payment events to Kafka topics consumed by:
 * - enrollment-service: to automatically enroll students
 * - notification-service: to send payment receipts and enrollment confirmations
 * - analytics-service: to track revenue and transactions
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Topic constants
    public static final String PAYMENT_SUCCESS_TOPIC = "payment-success";
    public static final String PAYMENT_FAILED_TOPIC = "payment-failed";
    public static final String PAYMENT_REFUNDED_TOPIC = "enrollment-refund";

    /**
     * Publishes payment-success event after successful payment processing.
     * This is the main trigger for:
     * 1. Enrollment creation
     * 2. Confirmation email
     * 3. Revenue analytics
     */
    public void publishPaymentSuccess(Payment payment) {
        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .paymentId(payment.getId())
                .studentId(payment.getStudentId())
                .courseId(payment.getCourseId())
                .courseTitle(payment.getCourseTitle())
                .instructorId(payment.getInstructorId())
                .amountPaid(payment.getAmount())
                .currency(payment.getCurrency())
                .transactionRef(payment.getTransactionId())
                .paidAt(payment.getCompletedAt())
                .build();

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(
                        PAYMENT_SUCCESS_TOPIC,
                        payment.getStudentId().toString(),
                        event
                );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published payment-success event: paymentId={}, topic={}, partition={}, offset={}",
                        payment.getId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("CRITICAL: Failed to publish payment-success event for paymentId: {}. Error: {}",
                        payment.getId(),
                        ex.getMessage(), ex);
                // todo - implement retry logic or store in outbox table
            }
        });
    }

    public void publishPaymentFailed(Payment payment) {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .paymentId(payment.getId())
                .studentId(payment.getStudentId())
                .courseId(payment.getCourseId())
                .failureReason(payment.getFailureReason())
                .failedAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send(PAYMENT_FAILED_TOPIC, payment.getStudentId().toString(), event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published payment-failed event: paymentId={}", payment.getId());
            } else {
                log.error("Failed to publish payment-failed event: {}", ex.getMessage());
            }
        });
    }

    public void publishPaymentRefunded(Payment payment) {
        PaymentRefundedEvent event = PaymentRefundedEvent.builder()
                .paymentId(payment.getId())
                .studentId(payment.getStudentId())
                .courseId(payment.getCourseId())
                .refundId(payment.getRefundId())
                .refundedAt(payment.getRefundedAt())
                .reason("Student requested refund")
                .build();

        kafkaTemplate.send(PAYMENT_REFUNDED_TOPIC, payment.getStudentId().toString(), event);
        log.info("Published payment-refunded event: paymentId={}", payment.getId());
    }

    // Event DTOs
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentSuccessEvent {
        private UUID paymentId;
        private UUID studentId;
        private UUID courseId;
        private String courseTitle;
        private UUID instructorId;
        private BigDecimal amountPaid;
        private String currency;
        private String transactionRef;
        private LocalDateTime paidAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentFailedEvent {
        private UUID paymentId;
        private UUID studentId;
        private UUID courseId;
        private String failureReason;
        private LocalDateTime failedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRefundedEvent {
        private UUID paymentId;
        private UUID studentId;
        private UUID courseId;
        private String refundId;
        private String reason;
        private LocalDateTime refundedAt;
    }

}
