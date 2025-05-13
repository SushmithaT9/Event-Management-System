package com.example.demo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Updated method to include event title
    public void sendRegistrationEmail(String toEmail, String userName, String eventTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("eventorganizers12@gmail.com");
        System.out.println("Sending email to " + toEmail);

        message.setTo(toEmail);
        message.setSubject("Thank you for registering!");
        message.setText("Hi " + userName + ",\n\nThank you for registering for our event: " 
                        + eventTitle + " 🎉🎉\n\nBest regards,\nEvent Organizers");

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();  // Print the stack trace to debug
        }
    }
}
