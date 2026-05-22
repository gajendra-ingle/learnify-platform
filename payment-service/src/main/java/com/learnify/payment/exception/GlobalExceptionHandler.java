package com.learnify.payment.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handlePaymentFailed(PaymentFailedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Payment failed | traceId={} | path={} | error={}", traceId, request.getRequestURI(), ex.getMessage());
        return buildResponse("PAYMENT_FAILED", ex.getMessage(), request, traceId);
    }


    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleInsufficientFunds(InsufficientFundsException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Insufficient funds | traceId={} | path={}", traceId, request.getRequestURI());
        return buildResponse("INSUFFICIENT_FUNDS", ex.getMessage(), request, traceId);
    }

    @ExceptionHandler(PaymentGatewayException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> handleGatewayError(PaymentGatewayException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("Gateway error | traceId={} | path={} | error={}", traceId, request.getRequestURI(), ex.getMessage());
        return buildResponse("GATEWAY_ERROR", "Payment service temporarily unavailable", request, traceId);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationError(Exception ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Validation error | traceId={} | path={}", traceId, request.getRequestURI());
        return buildResponse("VALIDATION_ERROR", "Invalid request data", request, traceId);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGenericException(Exception ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("Unexpected error | traceId={} | path={}", traceId, request.getRequestURI(), ex);
        return buildResponse("INTERNAL_SERVER_ERROR", "Something went wrong", request, traceId);
    }

    private Map<String, Object> buildResponse(String errorCode, String message, HttpServletRequest request, String traceId) {
        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", errorCode);
        response.put("message", message);
        response.put("path", request.getRequestURI());
        response.put("timestamp", LocalDateTime.now());
        response.put("traceId", traceId);
        return response;
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(SecurityException.class)
    public ProblemDetail handleSecurity(SecurityException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}