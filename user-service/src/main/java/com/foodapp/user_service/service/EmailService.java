package com.foodapp.user_service.service;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final static Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String email, String token) {
        logger.info("Sending password reset email to {}", email);
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Password Reset Request");
            helper.setText("Click the link to reset your password: " + resetLink, true);
            
            mailSender.send(message);
            logger.info("Password reset email sent successfully to {}", email);
        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", email, e.getMessage());
        }
    }

    public void sendRegistrationEmail(String email,String username){
        String registrationMessage = "Dear " + username + ",\n\n" +
                                      "Welcome to our platform! Your account has been successfully created.\n" +
                                      "You can now log in using your credentials.\n\n" +
                                      "Thank you for registering with us!\n\n" +
                                      "Best regards,\n" +
                                      "QuickBite Team";

                                    
                                      try{
                                        MimeMessage message = mailSender.createMimeMessage();
                                        MimeMessageHelper helper = new MimeMessageHelper(message,true);
                                        helper.setTo(email);
                                        helper.setSubject("Welcome To QuickBite - Registration Confirmation");
                                        helper.setText(registrationMessage,false);
                                        mailSender.send(message);
                                      }catch (Exception e) {
                                        e.printStackTrace();
                                    }
        
    }
}
