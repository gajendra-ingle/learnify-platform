package com.learnify.notification.service.impl;

import com.learnify.notification.kafka.NotificationEventConsumer.*;
import com.learnify.notification.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; // For HTML emails

    private static final String FROM_EMAIL = "noreply@learnify.com";
    private static final String FROM_NAME = "Learnify Learning Platform";

    @Override
    public void sendWelcomeEmail(UserRegisteredEvent event) {
        log.info("Sending welcome email to: {}", event.getEmail());

        String subject = "Welcome to Learnify, " + event.getFirstName() + "! 🎓";
        String body = buildWelcomeEmailBody(event);

        sendEmail(event.getEmail(), subject, body);
        log.info("Welcome email sent to: {}", event.getEmail());
    }

    @Override
    public void sendEnrollmentConfirmation(EnrollmentConfirmedEvent event) {
        log.info("Sending enrollment confirmation for enrollmentId: {}", event.getEnrollmentId());

        // In production: fetch user email from user-service
        // For now: log and proceed
        String subject = "You're enrolled in: " + event.getCourseTitle() + " ✅";
        String body = buildEnrollmentConfirmationBody(event);

        //sendEmail(userEmail, subject, body);
        log.info("Enrollment confirmation queued for courseId: {}, studentId: {}",
                event.getCourseId(), event.getStudentId());
    }

    // to-do
    @Override
    public void sendPaymentReceipt(com.learnify.notification.event.PaymentSuccessEvent event) {

    }

    public void sendPaymentReceipt(PaymentSuccessEvent event) {
        log.info("Sending payment receipt for paymentId: {}", event.getPaymentId());

        String subject = "Payment Receipt - " + event.getCourseTitle();
        log.info("Payment receipt queued: transactionRef={}, amount={} {}",
                event.getTransactionRef(), event.getAmountPaid(), event.getCurrency());
    }

    @Override
    public void sendCourseCompletionCertificate(EnrollmentCompletedEvent event) {
        log.info("Sending course completion certificate for enrollmentId: {}", event.getEnrollmentId());
        String subject = "Congratulations! You completed: " + event.getCourseTitle() + " 🏆";
        log.info("Certificate notification queued for studentId: {}, courseId: {}", event.getStudentId(), event.getCourseId());
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    private String buildWelcomeEmailBody(UserRegisteredEvent event) {
        return String.format("""
                Hi %s,
                
                Welcome to Learnify! 🎓
                
                Your account has been successfully created.
                Username: %s
                Email: %s
                Role: %s
                
                Start exploring thousands of courses at https://learnify.com/courses
                
                Happy Learning!
                The Learnify Team
                """,
                event.getFirstName(),
                event.getUsername(),
                event.getEmail(),
                event.getRole()
        );
    }

    private String buildEnrollmentConfirmationBody(EnrollmentConfirmedEvent event) {
        return String.format("""
                Enrollment Confirmed!
                
                Course: %s
                Enrollment ID: %s
                Enrolled At: %s
                Amount Paid: $%s
                
                Access your course at: https://learnify.com/my-courses
                
                Happy Learning!
                The Learnify Team
                """,
                event.getCourseTitle(),
                event.getEnrollmentId(),
                event.getEnrolledAt(),
                event.getAmountPaid()
        );
    }
}
