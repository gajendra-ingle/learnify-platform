package com.learnify.enrollment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Column(name = "course_title")
    private String courseTitle;

    @Column(name = "instructor_id")
    private UUID instructorId;

    @Column(name = "payment_id", unique = true)
    private UUID paymentId;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    @Column(name = "progress_percentage")
    private Double progressPercentage = 0.0;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "enrolled_at", updatable = false)
    private LocalDateTime enrolledAt;

}
