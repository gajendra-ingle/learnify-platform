package com.learnify.enrollment.controller;


import com.learnify.enrollment.dto.request.EnrollmentRequest;
import com.learnify.enrollment.dto.response.EnrollmentResponse;
import com.learnify.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Course enrollment management")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Enroll in a course")
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request, @RequestHeader("X-User-Id") UUID studentId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enrollStudent(studentId, request));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my enrollments")
    public ResponseEntity<Page<EnrollmentResponse>> getMyEnrollments(@RequestHeader("X-User-Id") UUID studentId, Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(studentId, pageable));
    }

    @PatchMapping("/{courseId}/progress")
    @Operation(summary = "Update course progress")
    public ResponseEntity<EnrollmentResponse> updateProgress(@PathVariable UUID courseId, @RequestParam Double progress, @RequestHeader("X-User-Id") UUID studentId) {
        return ResponseEntity.ok(enrollmentService.updateProgress(studentId, courseId, progress));
    }
}
