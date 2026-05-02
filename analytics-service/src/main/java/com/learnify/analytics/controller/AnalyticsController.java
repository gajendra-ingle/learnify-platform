package com.learnify.analytics.controller;

import com.learnify.analytics.dto.response.CourseAnalyticsResponse;
import com.learnify.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Platform analytics and reporting")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseAnalyticsResponse> getCourseAnalytics(@PathVariable UUID courseId) {
        return ResponseEntity.ok(analyticsService.getCourseAnalytics(courseId));
    }

    @GetMapping("/courses/top-revenue")
    public ResponseEntity<List<CourseAnalyticsResponse>> getTopCoursesByRevenue(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getTopCoursesByRevenue(limit));
    }

}
