package com.service.shared;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    // Define a constant for the sender email (must match your configured Spring Mail username)
    private static final String SENDER_EMAIL = "ITSM-Support@yourcompany.com";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a specific email notification for role assignment.
     */
    public void sendRoleAssignmentEmail(String toEmail, String roleName) {
        if (toEmail == null || toEmail.isBlank()) return;
        
        String subject = "Role Assigned in Incident Management System";
        String body = "Hi,\n\nYou have been assigned the role: " + roleName + ".\n\nRegards,\nIncident Management Team";
        
        sendEmail(toEmail, subject, body);
    }

    /**
     * Generic method to send a simple text email.
     * Includes error handling to prevent mail failure from breaking transaction.
     */
    public void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            System.err.println("EmailService: Failed to send email. Recipient address is null or empty for subject: " + subject);
            return;
        }
        
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(SENDER_EMAIL); // Set the sender address
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        } catch (MailException e) {
            // Log the error but allow the calling service transaction (e.g., incident creation) to commit.
            System.err.println("EmailService Error: Could not send email to " + to + " with subject [" + subject + "]. Error: " + e.getMessage());
        }
    }
}