package com.learnify.notification.event;

import com.learnify.notification.service.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final EmailServiceImpl emailService;

    @KafkaListener(
            topics = "${kafka.topics.enrollment-created:enrollment-created}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEnrollmentCreated(EnrollmentCreatedEvent event) {
        log.info("Processing enrollment notification for student: {}", event.getStudentId());
        if (event.getStudentEmail() != null) {
            emailService.sendEnrollmentConfirmation(
                    event.getStudentEmail(),
                    "Student",
                    event.getCourseTitle()
            );
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.payment-success:payment-success}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Processing payment notification for student: {}", event.getStudentId());
        // In production: fetch student email from user service
        // emailService.sendPaymentReceipt(studentEmail, ...);
    }

}
