package com.learnify.enrollment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class EnrollmentRequest {
    @NotNull(message = "Course ID is required")
    private UUID courseId;
}

