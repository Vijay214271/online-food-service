package com.foodapp.emailservice;

import com.foodapp.emailservice.dto.EmailRequestDTO;
import com.foodapp.emailservice.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private EmailRequestDTO emailRequest;

    @BeforeEach
    public void setUp() {
        emailRequest = new EmailRequestDTO();
        emailRequest.setTo("test@example.com");
        emailRequest.setSubject("Test Subject");
        emailRequest.setMessage("Test Message");
    }

    @Test
    public void testSendEmail_Success() throws MessagingException {
        // Arrange
        MimeMessage message = mock(MimeMessage.class);
        @SuppressWarnings("unused")
        MimeMessageHelper helper = mock(MimeMessageHelper.class);
        
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(mailSender.createMimeMessage()).thenReturn(message);
        doNothing().when(mailSender).send(message);

        // Act
        emailService.sendEmail(emailRequest);

        // Assert
        verify(mailSender, times(1)).send(message);
    }

    @Test
    public void testSendEmail_Failure() throws MessagingException {
        // Arrange
        MimeMessage message = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        
        // Create a MessagingException to be thrown as the cause
        MessagingException messagingException = new MessagingException("Sending failed");
        
        // Mock the behavior of the mailSender to throw a MessagingException wrapped in a RuntimeException
        doThrow(new RuntimeException("Failed to send email", messagingException)).when(mailSender).send(message);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailService.sendEmail(emailRequest));
        
        // Assert the message of the exception
        assertEquals("Failed to send email", exception.getMessage());
        
        // Assert that the cause is indeed a MessagingException
        assertTrue(exception.getCause() instanceof MessagingException);
        assertEquals("Sending failed", exception.getCause().getMessage());
    }
    
    
    
    
    
    
    

    @Test
    public void testSendEmail_InvalidEmailRequest() {
        // Arrange
        emailRequest.setTo(null); // Simulating invalid email request

        // Act & Assert
        assertThrows(NullPointerException.class, () -> emailService.sendEmail(emailRequest));
    }
}
