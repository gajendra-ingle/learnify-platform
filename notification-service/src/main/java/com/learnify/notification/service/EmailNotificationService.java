package com.learnify.notification.service;

import com.learnify.notification.event.PaymentSuccessEvent;
import com.learnify.notification.kafka.NotificationEventConsumer;

public interface EmailNotificationService {

    /**
     * Sends a welcome email to a newly registered user.
     *
     * @param event contains user registration details such as
     *              username, email, first name, and role
     */
    void sendWelcomeEmail(NotificationEventConsumer.UserRegisteredEvent event);

    /**
     * Sends an enrollment confirmation email after a student
     * successfully enrolls in a course.
     *
     * @param event contains enrollment details including
     *              course title, enrollment ID, payment amount,
     *              and enrollment timestamp
     */
    void sendEnrollmentConfirmation(NotificationEventConsumer.EnrollmentConfirmedEvent event);

    /**
     * Sends a payment receipt email after successful payment
     * processing for a course purchase.
     *
     * @param event contains payment transaction details such as
     *              payment ID, transaction reference, amount paid,
     *              currency, and course information
     */
    void sendPaymentReceipt(PaymentSuccessEvent event);

    /**
     * Sends a course completion and certificate notification
     * email to the student after course completion.
     *
     * @param event contains course completion details including
     *              enrollment ID, course ID, student ID,
     *              and completed course title
     */
    void sendCourseCompletionCertificate(NotificationEventConsumer.EnrollmentCompletedEvent event);

}
