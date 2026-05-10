package com.learnify.enrollment.dto;

import com.learnify.enrollment.entity.EnrollmentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class EnrollmentDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollRequest {
        @NotNull(message = "Course ID is required")
        private UUID courseId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProgressRequest {
        @NotNull
        @Min(0)
        @Max(100)
        private Integer progressPercentage;

        @NotNull
        @Min(0)
        private Integer completedLessons;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentResponse {
        private UUID id;
        private UUID studentId;
        private UUID courseId;
        private String courseTitle;
        private EnrollmentStatus status;
        private BigDecimal amountPaid;
        private Integer progressPercentage;
        private Integer completedLessons;
        private LocalDateTime enrolledAt;
        private LocalDateTime completedAt;
        private LocalDateTime lastAccessedAt;
        private String certificateUrl;
    }
}

