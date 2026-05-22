package com.learnify.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

}
