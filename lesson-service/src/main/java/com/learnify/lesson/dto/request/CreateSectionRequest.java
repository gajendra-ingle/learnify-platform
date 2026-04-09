package com.learnify.lesson.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateSectionRequest {

    @NotNull
    private UUID courseId;

    @NotBlank
    private String title;

    private Integer orderIndex;
}
