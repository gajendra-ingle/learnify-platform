package com.learnify.payment;

import com.learnify.payment.dto.request.PaymentRequest;
import com.learnify.payment.dto.response.PaymentResponse;
import com.learnify.payment.entity.Payment;
import com.learnify.payment.entity.PaymentStatus;
import com.learnify.payment.event.PaymentEventProducer;
import com.learnify.payment.exception.PaymentException;
import com.learnify.payment.repository.PaymentRepository;
import com.learnify.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Tests")
public class PaymentServiceTest {


    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentEventProducer eventProducer;

    @InjectMocks private PaymentService paymentService;

    private UUID studentId;
    private PaymentRequest request;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        request = new PaymentRequest();
        request.setCourseId(UUID.randomUUID());
        request.setAmount(BigDecimal.valueOf(49.99));
        request.setPaymentMethod("CREDIT_CARD");
        request.setCardToken("tok_test_123");
    }

    @Test
    @DisplayName("Should process payment and publish Kafka event")
    void shouldProcessPaymentSuccessfully() {
        when(paymentRepository.existsByStudentIdAndCourseIdAndStatus(
                any(), any(), eq(PaymentStatus.COMPLETED))).thenReturn(false);

        Payment savedPayment = Payment.builder()
                .id(UUID.randomUUID())
                .studentId(studentId)
                .courseId(request.getCourseId())
                .amount(request.getAmount())
                .status(PaymentStatus.PROCESSING)
                .build();

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment)
                .thenAnswer(inv -> {
                    Payment p = inv.getArgument(0);
                    p.setStatus(PaymentStatus.COMPLETED);
                    return p;
                });

        PaymentResponse response = paymentService.processPayment(studentId, request);

        assertThat(response).isNotNull();
        verify(eventProducer).publishPaymentSuccess(any());
    }

    @Test
    @DisplayName("Should reject duplicate payment for same course")
    void shouldRejectDuplicatePayment() {
        when(paymentRepository.existsByStudentIdAndCourseIdAndStatus(
                any(), any(), eq(PaymentStatus.COMPLETED))).thenReturn(true);

        assertThatThrownBy(() -> paymentService.processPayment(studentId, request))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("already purchased");
    }

}
