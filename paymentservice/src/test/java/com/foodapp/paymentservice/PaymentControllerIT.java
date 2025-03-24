package com.foodapp.paymentservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.paymentservice.config.JwtUtil;
import com.foodapp.paymentservice.controller.PaymentController;
import com.foodapp.paymentservice.dto.PaymentRequestDTO;
import com.foodapp.paymentservice.dto.PaymentResponseDTO;
import com.foodapp.paymentservice.dto.RefundRequestDTO;
import com.foodapp.paymentservice.entity.Payment;
import com.foodapp.paymentservice.repository.PaymentRepository;
import com.foodapp.paymentservice.service.PaymentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Loads application-test.properties

public class PaymentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        paymentRepository.deleteAll(); // Clean database before each test

        testPayment = Payment.builder()
                .id(101L)
                .orderId(101L)
                .amount(250.0)
                .paymentStatus("SUCCESS")
                .paymentMethod("CREDIT_CARD")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(paymentRepository.findById(101L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.findByOrderId(101L)).thenReturn(Optional.of(testPayment));
        

        testPayment = paymentRepository.save(testPayment);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testProcessPayment() throws Exception {
        String jwtToken ="your-valid-jwt-token";
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(101L, 300.0, "DEBIT_CARD");
        String username = "user";
        PaymentResponseDTO responseDTO = new PaymentResponseDTO
        (testPayment.getId(), testPayment.getOrderId(), testPayment.getAmount(), testPayment.getPaymentStatus(), testPayment.getPaymentMethod(), testPayment.getPaymentDate());

        // Mocking JwtUtil methods
        when(jwtUtil.extractUsername(jwtToken)).thenReturn(username);
        when(jwtUtil.validateToken(jwtToken)).thenReturn(true);
        when(paymentService.processPayment(any(PaymentRequestDTO.class))).thenReturn(responseDTO);
        // Set the authentication context manually, simulating the authenticated user
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()));

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer "+jwtToken)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(101L))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetPaymentById() throws Exception {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO
        (testPayment.getId(), testPayment.getOrderId(), testPayment.getAmount(), testPayment.getPaymentStatus(), testPayment.getPaymentMethod(), testPayment.getPaymentDate());

        when(paymentService.getPaymentById(101L)).thenReturn(responseDTO);
        mockMvc.perform(get("/api/payments/" + testPayment.getId()))
                .andDo(print())  // This will print the response body to the console
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetPaymentByOrderId() throws Exception {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO
        (testPayment.getId(), testPayment.getOrderId(), testPayment.getAmount(), testPayment.getPaymentStatus(), testPayment.getPaymentMethod(), testPayment.getPaymentDate());
        when(paymentService.getPaymentByOrderId(101L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/payments/order/" + testPayment.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(101L))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllPayments() throws Exception {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO
        (testPayment.getId(), testPayment.getOrderId(), testPayment.getAmount(), testPayment.getPaymentStatus(), testPayment.getPaymentMethod(), testPayment.getPaymentDate());
       
        when(paymentService.getAllPayments()).thenReturn(new ArrayList<PaymentResponseDTO>() {
            {
                add(responseDTO);
            }
        });
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRefundPayment() throws Exception {
        RefundRequestDTO refundRequest = new RefundRequestDTO(testPayment.getId());
        when(paymentService.refundRequest(refundRequest)).thenReturn(
            "Refund processed successfully"
        );
        mockMvc.perform(post("/api/payments/refund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refundRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Refund processed successfully")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdatePaymentStatus() throws Exception {
        Payment originalPayment = new Payment(101L, 101L, 250.0, "FAILED", "DEBIT_CARD", LocalDateTime.now());

        PaymentResponseDTO responseDTO = new PaymentResponseDTO
        (testPayment.getId(), testPayment.getOrderId(), testPayment.getAmount(), "FAILED", testPayment.getPaymentMethod(), testPayment.getPaymentDate());

        String newStatus = "FAILED";
        when(paymentRepository.findById(101L)).thenReturn(Optional.of(originalPayment));
        when(paymentService.updatePaymentStatus(any(), any(String.class))).thenReturn(responseDTO);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        mockMvc.perform(put("/api/payments/" + testPayment.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStatus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("FAILED"));
    }
}
