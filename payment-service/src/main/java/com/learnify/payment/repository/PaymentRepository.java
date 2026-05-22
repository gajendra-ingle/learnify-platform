package com.learnify.payment.repository;

import com.learnify.payment.entity.Payment;
import com.learnify.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Page<Payment> findByStudentId(UUID studentId, Pageable pageable);

    Optional<Payment> findByTransactionId(String transactionId);

    boolean existsByStudentIdAndCourseIdAndStatus(UUID studentId, UUID courseId, PaymentStatus status);

    List<Payment> findByStudentIdAndStatus(UUID studentId, PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.courseId = :courseId AND p.status = 'COMPLETED'")
    BigDecimal getTotalRevenueForCourse(UUID courseId);

    // <T> Optional<T> findById(UUID id);
}
