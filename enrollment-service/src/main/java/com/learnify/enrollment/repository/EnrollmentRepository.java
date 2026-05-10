package com.learnify.enrollment.repository;

import com.learnify.enrollment.entity.Enrollment;
import com.learnify.enrollment.entity.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    Optional<Enrollment> findByStudentIdAndCourseId(UUID studentId, UUID courseId);

    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    Page<Enrollment> findByStudentId(UUID studentId, Pageable pageable);

    Page<Enrollment> findByCourseId(UUID courseId, Pageable pageable);

    List<Enrollment> findByStudentIdAndStatus(UUID studentId, EnrollmentStatus status);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.courseId = :courseId AND e.status = 'ACTIVE'")
    long countActiveByCourseId(UUID courseId);

    Optional<Enrollment> findByPaymentId(UUID paymentId);
}
