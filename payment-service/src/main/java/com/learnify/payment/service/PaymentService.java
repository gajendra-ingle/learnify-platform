package com.learnify.payment.service;

import com.learnify.payment.dto.request.PaymentRequest;
import com.learnify.payment.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse processPayment(UUID studentId, @Valid PaymentRequest request);

    Page<PaymentResponse> getStudentPayments(UUID studentId, Pageable pageable);

    PaymentResponse getPaymentById(UUID id);

}
