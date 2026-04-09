package com.learnify.lesson.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponse {
    private UUID id;
    private UUID courseId;
    private String title;
    private Integer orderIndex;
    private List<LessonResponse> lessons;
}
