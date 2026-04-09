package com.learnify.lesson.controller;

import com.learnify.lesson.dto.request.CreateLessonRequest;
import com.learnify.lesson.dto.request.CreateSectionRequest;
import com.learnify.lesson.dto.response.LessonResponse;
import com.learnify.lesson.dto.response.SectionResponse;
import com.learnify.lesson.service.LessonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Lesson and section management")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/sections")
    public ResponseEntity<SectionResponse> createSection(
            @Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.createSection(request));
    }

    @GetMapping("/sections/course/{courseId}")
    public ResponseEntity<List<SectionResponse>> getCourseSections(@PathVariable UUID courseId) {
        return ResponseEntity.ok(lessonService.getCourseSections(courseId));
    }

    @PostMapping
    public ResponseEntity<LessonResponse> createLesson(
            @Valid @RequestBody CreateLessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.createLesson(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getLesson(@PathVariable UUID id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

}
