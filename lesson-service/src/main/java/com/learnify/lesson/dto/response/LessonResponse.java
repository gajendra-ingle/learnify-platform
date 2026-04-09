package com.learnify.lesson.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {
    private UUID id;
    private UUID sectionId;
    private String title;
    private String description;
    private String type;
    private String videoUrl;
    private Integer videoDurationSeconds;
    private String contentText;
    private Integer orderIndex;
    private boolean freePreview;
}
