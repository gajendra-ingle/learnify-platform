package com.learnify.payment.event;

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
public class PaymentSuccessEvent {

    private UUID paymentId;
    private UUID studentId;
    private UUID courseId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime paidAt;

}
