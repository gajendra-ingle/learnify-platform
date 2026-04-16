package com.learnify.auth.kafka;

import com.learnify.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public static final String USER_REGISTERED_TOPIC = "user-registered";
    public static final String USER_LOGIN_TOPIC = "user-login";

    public void publishUserRegisteredEvent(User user) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .registeredAt(LocalDateTime.now())
                .build();

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(USER_REGISTERED_TOPIC, user.getId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published user-registered event for userId: {} to partition: {}",
                        user.getId(), result.getRecordMetadata().partition());
            } else {
                log.error("Failed to publish user-registered event for userId: {}", user.getId(), ex);
            }
        });
    }

    public void publishUserLoginEvent(User user) {
        UserLoginEvent event = UserLoginEvent.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .loginAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send(USER_LOGIN_TOPIC, user.getId().toString(), event);
        log.debug("Published user-login event for userId: {}", user.getId());
    }
}
