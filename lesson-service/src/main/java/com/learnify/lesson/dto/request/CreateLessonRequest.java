package com.learnify.lesson.dto.request;

import com.learnify.lesson.entity.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateLessonRequest {

    @NotNull
    private UUID sectionId;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LessonType type;

    private String videoUrl;
    private Integer videoDurationSeconds;
    private String contentText;
    private Integer orderIndex;
    private boolean freePreview;
}
