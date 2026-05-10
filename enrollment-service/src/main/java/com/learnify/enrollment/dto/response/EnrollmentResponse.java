package com.learnify.enrollment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResponse {

    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private String courseTitle;
    private String status;
    private Double progressPercentage;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;

}
