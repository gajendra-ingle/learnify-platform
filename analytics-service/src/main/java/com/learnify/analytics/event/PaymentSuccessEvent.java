package com.learnify.analytics.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {

    private UUID paymentId;
    private UUID studentId;
    private UUID courseId;
    private BigDecimal amount;
    private LocalDateTime paidAt;

}
