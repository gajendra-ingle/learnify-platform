package com.learnify.notification.service.impl;

import com.learnify.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendEnrollmentConfirmation(String to, String studentName, String courseTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Enrollment Confirmed: " + courseTitle);
            helper.setText(buildEnrollmentEmailHtml(studentName, courseTitle), true);
            mailSender.send(message);
            log.info("Enrollment confirmation sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send enrollment email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendPaymentReceipt(String to, String studentName, String courseTitle, String amount, String transactionId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Payment Receipt - Learnify");
            helper.setText(buildPaymentReceiptHtml(studentName, courseTitle, amount, transactionId), true);
            mailSender.send(message);
            log.info("Payment receipt sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send payment receipt to {}: {}", to, e.getMessage());
        }
    }

    private String buildEnrollmentEmailHtml(String name, String course) {
        return """
            <html><body>
            <h2>Welcome to %s!</h2>
            <p>Dear %s,</p>
            <p>You have been successfully enrolled in <strong>%s</strong>.</p>
            <p>Start learning now at <a href="https://learnify.com">learnify.com</a></p>
            <p>Happy Learning!<br>The Learnify Team</p>
            </body></html>
            """.formatted(course, name, course);
    }

    private String buildPaymentReceiptHtml(String name, String course, String amount, String txnId) {
        return """
            <html><body>
            <h2>Payment Receipt</h2>
            <p>Dear %s,</p>
            <p>Your payment for <strong>%s</strong> has been processed successfully.</p>
            <table>
              <tr><td><strong>Amount:</strong></td><td>$%s</td></tr>
              <tr><td><strong>Transaction ID:</strong></td><td>%s</td></tr>
            </table>
            <p>Thank you for learning with Learnify!</p>
            </body></html>
            """.formatted(name, course, amount, txnId);
    }

}
