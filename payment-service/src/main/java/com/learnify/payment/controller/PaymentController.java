package com.learnify.payment.controller;


import com.learnify.payment.dto.request.PaymentRequest;
import com.learnify.payment.dto.response.PaymentResponse;
import com.learnify.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process a course payment")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request, @RequestHeader("X-User-Id") UUID studentId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.processPayment(studentId, request));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my payment history")
    public ResponseEntity<Page<PaymentResponse>> getMyPayments(@RequestHeader("X-User-Id") UUID studentId, Pageable pageable) {
        return ResponseEntity.ok(paymentService.getStudentPayments(studentId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}
