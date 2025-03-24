package com.foodapp.deliveryservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.deliveryservice.config.JwtUtil;
import com.foodapp.deliveryservice.controller.DeliveryController;
import com.foodapp.deliveryservice.dto.DeliveryRequestDTO;
import com.foodapp.deliveryservice.dto.DeliveryResponseDTO;
import com.foodapp.deliveryservice.model.Delivery;
import com.foodapp.deliveryservice.respository.DeliveryRepository;
import com.foodapp.deliveryservice.service.DeliveryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Loads application-test.properties
public class DeliveryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryController deliveryController;

    @Mock
    private DeliveryService deliveryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Delivery testDelivery;
    private DeliveryRequestDTO deliveryRequestDTO;
    private DeliveryResponseDTO deliveryResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(deliveryController).build();

        // Create a test delivery
        testDelivery = Delivery.builder()
                .id(1L)
                .orderId(1L)
                .deliveryPersonId(10L)
                .status("ON THE WAY")
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(30))
                .build();

        // Create a test request DTO
        deliveryRequestDTO = new DeliveryRequestDTO(1L, 10L, LocalDateTime.now().plusMinutes(30));

        // Create a test response DTO
        deliveryResponseDTO = new DeliveryResponseDTO(1L, 1L, 10L, "ON THE WAY", LocalDateTime.now().plusMinutes(30));

        // Mock repository responses
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(testDelivery);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(testDelivery));
        when(deliveryRepository.findByOrderId(1L)).thenReturn(Optional.of(testDelivery));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateDelivery() throws Exception {
        String jwtToken = "your-valid-jwt-token";

        // Mock JWT token validation
        when(jwtUtil.extractUsername(jwtToken)).thenReturn("admin");
        when(jwtUtil.validateToken(jwtToken)).thenReturn(true);

        // Mock service response
        when(deliveryService.createDelivery(any(DeliveryRequestDTO.class))).thenReturn(deliveryResponseDTO);

        // Set the authentication context manually
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, new ArrayList<>())
        );

        mockMvc.perform(post("/api/delivery/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(objectMapper.writeValueAsString(deliveryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("ON THE WAY"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetDeliveryById() throws Exception {
        // Mock service response
        when(deliveryService.getDeliveryById(1L)).thenReturn(deliveryResponseDTO);

        mockMvc.perform(get("/api/delivery/" + testDelivery.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON THE WAY"))
                .andExpect(jsonPath("$.deliveryPersonId").value(10L));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetDeliveryByOrderId() throws Exception {
        // Mock service response
        when(deliveryService.getDeliveryByOrderId(1L)).thenReturn(deliveryResponseDTO);

        mockMvc.perform(get("/api/delivery/order/" + testDelivery.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON THE WAY"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllDeliveries() throws Exception {
        // Mock service response
        when(deliveryService.getAllDeliveries()).thenReturn(List.of(deliveryResponseDTO));

        mockMvc.perform(get("/api/delivery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateDeliveryStatus() throws Exception {
        // Mock service response
        DeliveryResponseDTO updatedResponseDTO = new DeliveryResponseDTO(
            1L, 1L, 10L, "DELIVERED", LocalDateTime.now().plusMinutes(30));

        when(deliveryService.updateDeliveryStatus(1L, "DELIVERED")).thenReturn(updatedResponseDTO);

        mockMvc.perform(patch("/api/delivery/" + testDelivery.getId() + "/status")
                .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteDelivery() throws Exception {
        // Mock service response
        doNothing().when(deliveryService).deleteDelivery(1L);

        mockMvc.perform(delete("/api/delivery/" + testDelivery.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveDelivery() throws Exception {
        // Mock service response
        when(deliveryService.saveDelivery(any(DeliveryRequestDTO.class))).thenReturn(deliveryResponseDTO);

        mockMvc.perform(post("/api/delivery/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliveryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON THE WAY"));
    }
}