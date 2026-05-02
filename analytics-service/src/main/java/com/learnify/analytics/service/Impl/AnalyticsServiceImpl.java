package com.learnify.analytics.service.Impl;

import com.learnify.analytics.exception.CourseNotFoundException;
import com.learnify.analytics.service.AnalyticsService;

import com.learnify.analytics.dto.response.CourseAnalyticsResponse;
import com.learnify.analytics.repository.CourseAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final CourseAnalyticsRepository analyticsRepository;

    @Override
    @Transactional(readOnly = true)
    public CourseAnalyticsResponse getCourseAnalytics(UUID courseId) {
        return analyticsRepository.findByCourseId(courseId)
                .map(a -> CourseAnalyticsResponse.builder()
                        .courseId(a.getCourseId())
                        .totalEnrollments(a.getTotalEnrollments())
                        .activeStudents(a.getActiveStudents())
                        .completionRate(a.getCompletionRate())
                        .totalRevenue(a.getTotalRevenue())
                        .averageRating(a.getAverageRating())
                        .lastUpdated(a.getLastUpdated())
                        .build())
                .orElse(null);
                //.orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseAnalyticsResponse> getTopCoursesByRevenue(int limit) {
        return analyticsRepository.findTopCoursesByRevenue(Pageable.ofSize(limit))
                .stream()
                .map(a -> CourseAnalyticsResponse.builder()
                        .courseId(a.getCourseId())
                        .totalRevenue(a.getTotalRevenue())
                        .totalEnrollments(a.getTotalEnrollments())
                        .build())
                .collect(Collectors.toList());
    }

}
