package com.learnify.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "course_analytics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "course_id", nullable = false, unique = true)
    private UUID courseId;

    @Column(name = "total_enrollments")
    private Integer totalEnrollments = 0;

    @Column(name = "active_students")
    private Integer activeStudents = 0;

    @Column(name = "completion_rate")
    private Double completionRate = 0.0;

    @Column(name = "total_revenue", precision = 12, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

}
