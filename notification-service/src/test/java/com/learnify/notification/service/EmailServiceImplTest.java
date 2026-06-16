package com.learnify.notification.service;

import com.learnify.notification.service.impl.EmailServiceImpl;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        Session session = Session.getDefaultInstance(new Properties());
        mimeMessage = new MimeMessage(session);
    }

    @Test
    void sendEnrollmentConfirmation_ShouldSendEmailSuccessfully() {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEnrollmentConfirmation(
                "john@example.com",
                "John Doe",
                "Spring Boot Masterclass"
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEnrollmentConfirmation_WhenSendFails_ShouldNotThrowException() {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailSendException("SMTP Error"))
                .when(mailSender)
                .send(any(MimeMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendEnrollmentConfirmation(
                        "john@example.com",
                        "John Doe",
                        "Spring Boot Masterclass"
                ));

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendPaymentReceipt_ShouldSendEmailSuccessfully() {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPaymentReceipt(
                "john@example.com",
                "John Doe",
                "Spring Boot Masterclass",
                "99.99",
                "TXN123"
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendPaymentReceipt_WhenSendFails_ShouldNotThrowException() {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailSendException("SMTP Error"))
                .when(mailSender)
                .send(any(MimeMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendPaymentReceipt(
                        "john@example.com",
                        "John Doe",
                        "Spring Boot Masterclass",
                        "99.99",
                        "TXN123"
                ));

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEnrollmentConfirmation_ShouldSetCorrectSubject() throws Exception {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEnrollmentConfirmation(
                "john@example.com",
                "John Doe",
                "Spring Boot Masterclass"
        );

        assertEquals(
                "Enrollment Confirmed: Spring Boot Masterclass",
                mimeMessage.getSubject()
        );
    }

    @Test
    void sendPaymentReceipt_ShouldSetCorrectSubject() throws Exception {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPaymentReceipt(
                "john@example.com",
                "John Doe",
                "Spring Boot Masterclass",
                "99.99",
                "TXN123"
        );

        assertEquals(
                "Payment Receipt - Learnify",
                mimeMessage.getSubject()
        );
    }
}
