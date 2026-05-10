package com.learnify.enrollment.service;

import com.learnify.enrollment.dto.request.EnrollmentRequest;
import com.learnify.enrollment.dto.response.EnrollmentResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface EnrollmentService {

    EnrollmentResponse enrollStudent(UUID studentId, @Valid EnrollmentRequest request);

    Page<EnrollmentResponse> getStudentEnrollments(UUID studentId, Pageable pageable);

    EnrollmentResponse updateProgress(UUID studentId, UUID courseId, Double progress);

    void enrollStudentAfterPayment(UUID studentId, UUID courseId);

    void enrollAfterPayment(UUID studentId, UUID courseId, UUID paymentId, BigDecimal amountPaid);

    void cancelEnrollmentByPayment(UUID paymentId);
}
