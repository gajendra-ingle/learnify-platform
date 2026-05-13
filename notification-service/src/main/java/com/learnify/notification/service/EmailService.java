package com.learnify.notification.service;

public interface EmailService {

    /**
     * Sends an enrollment confirmation email asynchronously.
     *
     * @param to recipient email address
     * @param studentName name of the enrolled student
     * @param courseTitle title of the enrolled course
     */
    void sendEnrollmentConfirmation(String to, String studentName, String courseTitle);

    /**
     * Sends a payment receipt email asynchronously.
     *
     * @param to recipient email address
     * @param studentName name of the student
     * @param courseTitle purchased course title
     * @param amount payment amount
     * @param transactionId payment transaction ID
     */
    void sendPaymentReceipt(String to, String studentName, String courseTitle, String amount, String transactionId);
}
