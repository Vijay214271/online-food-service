package com.foodapp.emailservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.emailservice.dto.EmailRequestDTO;
import com.foodapp.emailservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = EmailserviceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService; // Mock the EmailService in the Spring context

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    @WithMockUser(roles = "USER") // Simulate a user with 'USER' role
    void testSendEmail() throws Exception {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO("test@example.com", "Subject", "Body message");
        doNothing().when(emailService).sendEmail(emailRequest); // Mock sendEmail to do nothing

        // Act & Assert
        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully"));

        // Verify that sendEmail was called once
        verify(emailService, times(1)).sendEmail(emailRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simulate a user with 'ADMIN' role
    void testGetEmailLogs() throws Exception {
        // Arrange
        String id = "123";

        // Act & Assert
        mockMvc.perform(get("/api/email/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Email log for ID: 123"));
    }
}
