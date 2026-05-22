package com.learnify.payment.service.impl;

import com.learnify.payment.dto.request.PaymentRequest;
import com.learnify.payment.dto.response.PaymentResponse;
import com.learnify.payment.entity.Payment;
import com.learnify.payment.entity.PaymentStatus;
import com.learnify.payment.event.PaymentEventProducer;
import com.learnify.payment.event.PaymentSuccessEvent;
import com.learnify.payment.exception.PaymentException;
import com.learnify.payment.repository.PaymentRepository;
import com.learnify.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer eventProducer;

    @Override
    @Transactional
    public PaymentResponse processPayment(UUID studentId, PaymentRequest request) {
        // Check for duplicate payment
        if (paymentRepository.existsByStudentIdAndCourseIdAndStatus(
                studentId, request.getCourseId(), PaymentStatus.COMPLETED)) {
            throw new PaymentException("Course already purchased");
        }

        Payment payment = Payment.builder()
                .studentId(studentId)
                .courseId(request.getCourseId())
                .amount(request.getAmount())
                .currency("USD")
                .status(PaymentStatus.PROCESSING)
                .paymentMethod(request.getPaymentMethod())
                .build();

        payment = paymentRepository.save(payment);

        try {
            // Simulate payment gateway integration (Stripe/PayPal)
            String transactionId = processWithGateway(request.getCardToken(), request.getAmount());

            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(transactionId);
            payment.setCompletedAt(LocalDateTime.now());
            payment = paymentRepository.save(payment);

            // Publish success event to Kafka -> triggers enrollment
            eventProducer.publishPaymentSuccess(PaymentSuccessEvent.builder()
                    .paymentId(payment.getId())
                    .studentId(studentId)
                    .courseId(request.getCourseId())
                    .amount(request.getAmount())
                    .currency("USD")
                    .paidAt(payment.getCompletedAt())
                    .build());

            log.info("Payment completed: {} for student: {} course: {}",
                    payment.getId(), studentId, request.getCourseId());

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            log.error("Payment failed: {}", e.getMessage());
            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getStudentPayments(UUID studentId, Pageable pageable) {
        return paymentRepository.findByStudentId(studentId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new PaymentException("Payment not found"));
    }

    // ToDo
    private String processWithGateway(String cardToken, java.math.BigDecimal amount) {
        // Integration point for Stripe/PayPal SDK
        // In production: call Stripe.charges.create()
        return "txn_" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16);
    }

    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .studentId(p.getStudentId())
                .courseId(p.getCourseId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .status(p.getStatus().name())
                .transactionId(p.getTransactionId())
                .createdAt(p.getCreatedAt())
                .completedAt(p.getCompletedAt())
                .build();
    }


}
