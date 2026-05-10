package com.learnify.enrollment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.enrollment-created:enrollment-created}")
    private String enrollmentCreatedTopic;

    public void publishEnrollmentCreated(EnrollmentCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(enrollmentCreatedTopic, event.getEnrollmentId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish enrollment event for enrollmentId: {}", event.getEnrollmentId(), ex);
            } else {
                log.info("Enrollment event published: {} to partition {}", event.getEnrollmentId(), result.getRecordMetadata().partition());
            }
        });
    }
}

