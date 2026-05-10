package com.learnify.enrollment.event;

import com.learnify.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentEventConsumer {

    private final EnrollmentService enrollmentService;

    @KafkaListener(
            topics = "${kafka.topics.payment-success:payment-success}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Received payment success event for student: {} course: {}",
                event.getStudentId(), event.getCourseId());
        try {
            enrollmentService.enrollStudentAfterPayment(event.getStudentId(), event.getCourseId());
        } catch (Exception e) {
            log.error("Failed to process payment success event: {}", e.getMessage(), e);
        }
    }
}

