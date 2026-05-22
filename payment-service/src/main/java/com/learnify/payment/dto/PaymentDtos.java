package com.learnify.payment.dto;

import com.learnify.payment.entity.PaymentMethod;
import com.learnify.payment.entity.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitiatePaymentRequest {
        @NotNull(message = "Course ID is required")
        private UUID courseId;

        @NotBlank(message = "Course title is required")
        private String courseTitle;

        private UUID instructorId;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;

        private String currency;

        @NotBlank(message = "Payment method is required")
        private String paymentMethod;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private UUID paymentId;
        private String transactionRef;
        private UUID courseId;
        private String courseTitle;
        private BigDecimal amount;
        private String currency;
        private PaymentStatus status;
        private PaymentMethod paymentMethod;
        private String clientSecret; // For Stripe frontend
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebhookPayload {
        private String eventType;
        private String transactionRef;
        private String gatewayPaymentId;
        private String status;
        private String signature;
    }

}
