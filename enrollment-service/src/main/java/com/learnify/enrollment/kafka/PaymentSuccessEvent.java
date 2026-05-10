package com.learnify.enrollment.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {

    private UUID paymentId;
    private UUID studentId;
    private UUID courseId;
    private String courseTitle;
    private UUID instructorId;
    private BigDecimal amountPaid;
    private String currency;
    private LocalDateTime paidAt;
    private String transactionRef;

}
