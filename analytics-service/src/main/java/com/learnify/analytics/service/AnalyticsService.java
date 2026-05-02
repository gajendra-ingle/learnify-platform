package com.learnify.analytics.service;

import com.learnify.analytics.dto.response.CourseAnalyticsResponse;

import java.util.List;
import java.util.UUID;

public interface AnalyticsService {

    CourseAnalyticsResponse getCourseAnalytics(UUID courseId);

    List<CourseAnalyticsResponse> getTopCoursesByRevenue(int limit);
}
