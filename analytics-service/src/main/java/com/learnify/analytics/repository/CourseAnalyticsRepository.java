package com.learnify.analytics.repository;

import com.learnify.analytics.entity.CourseAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseAnalyticsRepository extends JpaRepository<CourseAnalytics, UUID> {

    Optional<CourseAnalytics> findByCourseId(UUID courseId);

    @Query("SELECT c FROM CourseAnalytics c ORDER BY c.totalRevenue DESC")
    List<CourseAnalytics> findTopCoursesByRevenue(org.springframework.data.domain.Pageable pageable);

}
