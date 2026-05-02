package com.learnify.analytics.dto.response;

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
public class CourseAnalyticsResponse {

    private UUID courseId;
    private Integer totalEnrollments;
    private Integer activeStudents;
    private Double completionRate;
    private BigDecimal totalRevenue;
    private Double averageRating;
    private LocalDateTime lastUpdated;

}
