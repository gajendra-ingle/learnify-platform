package com.learnify.enrollment.event;

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
public class EnrollmentCreatedEvent {

    private UUID enrollmentId;
    private UUID studentId;
    private UUID courseId;
    private String courseTitle;
    private String studentEmail;
    private LocalDateTime enrolledAt;

}
