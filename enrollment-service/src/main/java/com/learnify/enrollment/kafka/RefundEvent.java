package com.learnify.enrollment.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundEvent {

    private UUID paymentId;
    private UUID studentId;
    private UUID courseId;
    private String reason;
    private LocalDateTime refundedAt;

}
