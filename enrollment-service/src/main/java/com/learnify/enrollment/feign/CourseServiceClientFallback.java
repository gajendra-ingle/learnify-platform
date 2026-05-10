package com.learnify.enrollment.feign;

import com.learnify.enrollment.feign.dto.CourseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Fallback implementation for Course Service Feign Client.
 * Called when the circuit is open or course-service is unreachable.
 */
@Component
@Slf4j
public class CourseServiceClientFallback implements CourseServiceClient {

    @Override
    public ResponseEntity<CourseDto> getCourseById(UUID courseId) {
        log.warn("Course service unavailable, returning fallback for courseId: {}", courseId);
        return ResponseEntity.ok(null); // Or return a cached/default response
    }

    @Override
    public ResponseEntity<Boolean> courseExists(UUID courseId) {
        log.warn("Course service unavailable, cannot verify courseId: {}", courseId);
        // Fail-safe - assume course exists to not block enrollments
        return ResponseEntity.ok(true);
    }
}
