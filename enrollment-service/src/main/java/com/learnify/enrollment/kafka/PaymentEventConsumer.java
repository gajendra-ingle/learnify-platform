package com.learnify.enrollment.kafka;


import com.learnify.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer for Payment Events.
 *
 * Listens to the "payment-success" topic and automatically enrolls
 * students after a successful payment.
 *
 * Uses manual acknowledgment to ensure exactly-once processing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final EnrollmentService enrollmentService;

    @KafkaListener(
            topics = "payment-success",
            groupId = "enrollment-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentSuccess(
            @Payload PaymentSuccessEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("Received payment-success event: paymentId={}, studentId={}, courseId={}, partition={}, offset={}",
                event.getPaymentId(), event.getStudentId(), event.getCourseId(), partition, offset);

        try {
            enrollmentService.enrollAfterPayment(
                    event.getStudentId(),
                    event.getCourseId(),
                    event.getPaymentId(),
                    event.getAmountPaid()
            );

            // Acknowledge only after successful processing (manual ack mode)
            acknowledgment.acknowledge();
            log.info("Payment event processed and acknowledged: paymentId={}", event.getPaymentId());
        } catch (Exception e) {
            log.error("Failed to process payment event: paymentId={}, error={}", event.getPaymentId(), e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "enrollment-refund",
            groupId = "enrollment-service-group"
    )
    public void handleEnrollmentRefund(@Payload RefundEvent event, Acknowledgment acknowledgment) {
        log.info("Received refund event: paymentId={}, studentId={}", event.getPaymentId(), event.getStudentId());
        try {
            enrollmentService.cancelEnrollmentByPayment(event.getPaymentId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process refund event: {}", e.getMessage(), e);
        }
    }
}

