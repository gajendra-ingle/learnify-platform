package com.learnify.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.payment-success:payment-success}")
    private String paymentSuccessTopic;

    @Value("${kafka.topics.payment-failed:payment-failed}")
    private String paymentFailedTopic;

    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        kafkaTemplate.send(paymentSuccessTopic, event.getPaymentId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish payment success event: {}", event.getPaymentId(), ex);
                    } else {
                        log.info("Payment success event published: {} -> topic: {} partition: {}",
                                event.getPaymentId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition());
                    }
                });
    }
}
