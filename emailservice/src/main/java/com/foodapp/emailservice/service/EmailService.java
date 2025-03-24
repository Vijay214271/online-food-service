package com.foodapp.emailservice.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.foodapp.emailservice.dto.EmailRequestDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender; // Ensured that JavaMailSender is final and injected

    public void sendEmail(EmailRequestDTO emailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(emailRequest.getMessage(), true); // Setting HTML format in the message
            mailSender.send(message); // Send email using JavaMailSender
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e); // Throw RuntimeException instead
        }
    }
}
