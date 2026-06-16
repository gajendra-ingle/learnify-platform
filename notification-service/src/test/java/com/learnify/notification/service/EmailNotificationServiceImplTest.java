package com.learnify.notification.service;

import com.learnify.notification.kafka.NotificationEventConsumer.EnrollmentCompletedEvent;
import com.learnify.notification.kafka.NotificationEventConsumer.EnrollmentConfirmedEvent;
import com.learnify.notification.kafka.NotificationEventConsumer.PaymentSuccessEvent;
import com.learnify.notification.kafka.NotificationEventConsumer.UserRegisteredEvent;
import com.learnify.notification.service.impl.EmailNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailNotificationServiceImpl service;

    private UserRegisteredEvent userRegisteredEvent;
    private EnrollmentConfirmedEvent enrollmentConfirmedEvent;
    private PaymentSuccessEvent paymentSuccessEvent;
    private EnrollmentCompletedEvent enrollmentCompletedEvent;

    @BeforeEach
    void setUp() {

        enrollmentConfirmedEvent = new EnrollmentConfirmedEvent();
        enrollmentConfirmedEvent.setEnrollmentId(UUID.randomUUID());
        enrollmentConfirmedEvent.setCourseId(UUID.randomUUID());
        enrollmentConfirmedEvent.setStudentId(UUID.randomUUID());
        enrollmentConfirmedEvent.setCourseTitle("Spring Boot Masterclass");
        enrollmentConfirmedEvent.setAmountPaid("99.99");
        enrollmentConfirmedEvent.setEnrolledAt(LocalDateTime.now());

        paymentSuccessEvent = new PaymentSuccessEvent();
        paymentSuccessEvent.setPaymentId(UUID.randomUUID());
        paymentSuccessEvent.setCourseId(UUID.randomUUID());
        paymentSuccessEvent.setStudentId(UUID.randomUUID());
        paymentSuccessEvent.setCourseTitle("Spring Boot Masterclass");
        paymentSuccessEvent.setTransactionRef("TXN-123");
        paymentSuccessEvent.setAmountPaid(new BigDecimal("99.99"));
        paymentSuccessEvent.setCurrency("USD");

        enrollmentCompletedEvent = new EnrollmentCompletedEvent();
        enrollmentCompletedEvent.setEnrollmentId(UUID.randomUUID());
        enrollmentCompletedEvent.setStudentId(UUID.randomUUID());
        enrollmentCompletedEvent.setCourseId(UUID.randomUUID());
        enrollmentCompletedEvent.setCourseTitle("Spring Boot Masterclass");
    }

    @Test
    void sendWelcomeEmail_ShouldSendEmailSuccessfully() {
        service.sendWelcomeEmail(userRegisteredEvent);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals("john@example.com", message.getTo()[0]);
        assertTrue(message.getSubject().contains("Welcome to Learnify"));
        assertTrue(message.getText().contains("John"));
        assertTrue(message.getText().contains("john123"));
    }

    @Test
    void sendWelcomeEmail_WhenMailSenderThrowsException_ShouldNotThrow() {
        doThrow(new MailSendException("SMTP Error"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                service.sendWelcomeEmail(userRegisteredEvent));

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEnrollmentConfirmation_ShouldExecuteSuccessfully() {
        assertDoesNotThrow(() ->
                service.sendEnrollmentConfirmation(enrollmentConfirmedEvent));
    }

    @Test
    void sendPaymentReceipt_ShouldExecuteSuccessfully() {
        assertDoesNotThrow(() ->
                service.sendPaymentReceipt(paymentSuccessEvent));
    }

    @Test
    void sendCourseCompletionCertificate_ShouldExecuteSuccessfully() {
        assertDoesNotThrow(() ->
                service.sendCourseCompletionCertificate(enrollmentCompletedEvent));
    }

    @Test
    void sendWelcomeEmail_ShouldGenerateCorrectSubject() {
        service.sendWelcomeEmail(userRegisteredEvent);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        assertEquals(
                "Welcome to Learnify, John! 🎓",
                captor.getValue().getSubject()
        );
    }

    @Test
    void sendWelcomeEmail_ShouldContainAllUserDetails() {
        service.sendWelcomeEmail(userRegisteredEvent);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        String body = captor.getValue().getText();

        assertAll(
                () -> assertTrue(body.contains("John")),
                () -> assertTrue(body.contains("john123")),
                () -> assertTrue(body.contains("john@example.com")),
                () -> assertTrue(body.contains("STUDENT"))
        );
    }

    @Test
    void sendWelcomeEmail_WithDifferentUserData() {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setFirstName("Alice");
        event.setUsername("alice123");
        event.setEmail("alice@test.com");
        event.setRole("INSTRUCTOR");

        service.sendWelcomeEmail(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendWelcomeEmail_WhenEmailIsEmpty_ShouldStillAttemptSend() {
        userRegisteredEvent.setEmail("");
        service.sendWelcomeEmail(userRegisteredEvent);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
