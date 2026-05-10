package com.learnify.enrollment.client;

import com.learnify.enrollment.dto.response.CourseResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(
        name = "course-service",
        fallback = CourseServiceClient.CourseServiceFallback.class
)
public interface CourseServiceClient {

    @GetMapping("/api/courses/{id}")
    @CircuitBreaker(name = "course-service", fallbackMethod = "getCourseByIdFallback")
    CourseResponse getCourseById(@PathVariable UUID id);

    @Slf4j
    @org.springframework.stereotype.Component
    class CourseServiceFallback implements CourseServiceClient {

        @Override
        public CourseResponse getCourseById(UUID id) {
            log.warn("Course service fallback triggered for courseId: {}", id);
            return null;
        }
    }
}
