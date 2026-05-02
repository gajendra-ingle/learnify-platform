package com.learnify.analytics.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentCreatedEvent {

    private UUID enrollmentId;
    private UUID studentId;
    private UUID courseId;
    private String courseTitle;
    private LocalDateTime enrolledAt;

}
