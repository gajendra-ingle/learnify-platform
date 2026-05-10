package com.learnify.enrollment.feign;


import com.learnify.enrollment.feign.dto.CourseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Feign Client for Course Service communication.
 * Uses Eureka service discovery to locate the course-service.
 * Circuit breaker is configured via Resilience4j.
 */
@FeignClient(name = "course-service", path = "/api/v1/courses", fallback = CourseServiceClientFallback.class)
public interface CourseServiceClient {

    @GetMapping("/internal/{courseId}")
    ResponseEntity<CourseDto> getCourseById(@PathVariable UUID courseId);

    @GetMapping("/{courseId}/exists")
    ResponseEntity<Boolean> courseExists(@PathVariable UUID courseId);
}
