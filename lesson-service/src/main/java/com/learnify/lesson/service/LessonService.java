package com.learnify.lesson.service;

import com.learnify.lesson.dto.request.CreateLessonRequest;
import com.learnify.lesson.dto.request.CreateSectionRequest;
import com.learnify.lesson.dto.response.LessonResponse;
import com.learnify.lesson.dto.response.SectionResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    @Nullable
    SectionResponse createSection(@Valid CreateSectionRequest request);

    @Nullable
    List<SectionResponse> getCourseSections(UUID courseId);

    @Nullable
    LessonResponse createLesson(@Valid CreateLessonRequest request);

    @Nullable
    LessonResponse getLessonById(UUID id);
}
