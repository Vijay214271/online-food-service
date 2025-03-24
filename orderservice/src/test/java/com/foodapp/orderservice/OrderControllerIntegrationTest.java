package com.foodapp.orderservice;

import com.foodapp.orderservice.dto.OrderRequestDTO;
import com.foodapp.orderservice.dto.OrderResponseDTO;
import com.foodapp.orderservice.model.Order;
import com.foodapp.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Use @SpringBootTest for full integration testing
@ActiveProfiles("test") // Loads application-test.properties

public class OrderControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean // Use @MockBean to mock dependencies
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderResponseDTO orderResponseDTO;
    private OrderRequestDTO orderRequestDTO;
    private Order order;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); // Initialize MockMvc

        orderResponseDTO = new OrderResponseDTO(
                1L, 1L, "user@example.com", 2L, 100.0, "PENDING", LocalDateTime.now()
        );

        orderRequestDTO = new OrderRequestDTO(
                1L, "user@example.com", 2L, 100.0, "PENDING", LocalDateTime.now()
        );

        order = new Order(
                1L, "user@example.com", 1L, 2L, 100.0, "PENDING", LocalDateTime.now()
        );
    }

    // Test for fetching all orders (Admin only)
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllOrders_Admin() throws Exception {
        // Mock service response
        Mockito.when(orderService.getAllOrders()).thenReturn(List.of(orderResponseDTO));

        // Perform GET request and verify response
        mockMvc.perform(get("/order/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(orderResponseDTO.getId()))
                .andExpect(jsonPath("$[0].userEmail").value(orderResponseDTO.getUserEmail()));
    }

    // Test for fetching an order by ID (Admin or User)
    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testGetOrderById_User() throws Exception {
        // Mock service response
        Mockito.when(orderService.getOrderById(1L)).thenReturn(orderResponseDTO);

        // Perform GET request and verify response
        mockMvc.perform(get("/order/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderResponseDTO.getId()))
                .andExpect(jsonPath("$.userEmail").value(orderResponseDTO.getUserEmail()));
    }

    // Test for creating a new order (User only)
    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testCreateOrder_User() throws Exception {
        // Mock service response
        Mockito.when(orderService.createOrder(Mockito.any(OrderRequestDTO.class))).thenReturn(orderResponseDTO);

        // Perform POST request and verify response
        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderResponseDTO.getId()))
                .andExpect(jsonPath("$.status").value(orderResponseDTO.getStatus()));
    }

    // Test for updating an order (Admin only)
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateOrder_Admin() throws Exception {
        // Mock service response
        Mockito.when(orderService.updateOrder(Mockito.eq(1L), Mockito.any(OrderRequestDTO.class)))
                .thenReturn(orderResponseDTO);

        // Perform PUT request and verify response
        mockMvc.perform(put("/order/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderResponseDTO.getId()))
                .andExpect(jsonPath("$.status").value(orderResponseDTO.getStatus()));
    }

    // Test for placing an order (User only)
    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testPlaceOrder_User() throws Exception {
        // Mock service response
        Mockito.when(orderService.placeOrder(Mockito.any(Order.class))).thenReturn(order);

        // Perform POST request and verify response
        mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.status").value(order.getStatus()));
    }

    // Test for deleting an order (Admin or User)
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteOrder_Admin() throws Exception {
        // Mock service response
        Mockito.doNothing().when(orderService).deleteOrder(1L);

        // Perform DELETE request and verify response
        mockMvc.perform(delete("/order/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    // Test for exception handling (e.g., order not found)
    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testHandleException_OrderNotFound() throws Exception {
        // Mock service to throw an exception
        Mockito.when(orderService.getOrderById(999L)).thenThrow(new RuntimeException("999"));

        // Perform GET request and verify error response
        mockMvc.perform(get("/order/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Order with ID 999 not found"))
                .andExpect(jsonPath("$.details").value("Order Not Found"));
    }
}