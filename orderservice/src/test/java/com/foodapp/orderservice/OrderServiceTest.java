package com.foodapp.orderservice;

import com.foodapp.orderservice.client.EmailClient;
import com.foodapp.orderservice.client.PaymentClient;
import com.foodapp.orderservice.dto.*;
import com.foodapp.orderservice.model.Order;
import com.foodapp.orderservice.repository.OrderRepository;
import com.foodapp.orderservice.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private EmailClient emailClient;
    
    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private OrderService orderService;

    private OrderRequestDTO orderRequestDTO;
    private Order order;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        orderRequestDTO = new OrderRequestDTO(
                1L,
                "user@example.com",
                2L,
                100.0,
                "PENDING",
                LocalDateTime.now()
        );
        
        order = new Order(1L, "user@example.com", 1L, 2L, 100.0, "PENDING", LocalDateTime.now());
        
        // Initialize the service
        ReflectionTestUtils.setField(orderService, "orderRepository", orderRepository);
        ReflectionTestUtils.setField(orderService, "emailClient", emailClient);
        ReflectionTestUtils.setField(orderService, "paymentClient", paymentClient);
    }

    @Test
    public void testCreateOrder() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        EmailRequest emailRequest = new EmailRequest(
                order.getUserEmail(),
                "Order Confirmation - #" + order.getId(),
                "Dear customer,\n\nYour order has been placed successfully! Your order ID is " + order.getId() + ".\n\nThank you for choosing us!"
        );

        // Act
        OrderResponseDTO result = orderService.createOrder(orderRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals("PENDING", result.getStatus());

        // Verify email was sent
        verify(emailClient, times(1)).sendEmail(emailRequest);
    }

    @Test
    public void testGetAllOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(order));

        // Act
        List<OrderResponseDTO> orders = orderService.getAllOrders();

        // Assert
        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    @Test
    public void testGetOrderById() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        OrderResponseDTO result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetOrderById_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> orderService.getOrderById(1L));
        assertEquals("Order with ID 1 not found", thrown.getMessage());
    }

    @Test
    public void testUpdateOrder() {
        // Arrange
        OrderRequestDTO updatedOrderRequest = new OrderRequestDTO(1L, "user@example.com", 2L, 200.0, "CONFIRMED", LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponseDTO result = orderService.updateOrder(1L, updatedOrderRequest);

        // Assert
        assertNotNull(result);
        assertEquals(200.0, result.getTotalAmount());
        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    public void testDeleteOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteOrder_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> orderService.deleteOrder(1L));
        assertEquals("Order with ID 1 does not exist", thrown.getMessage());
    }

    @Test
    public void testPlaceOrder_SuccessfulPayment() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(new PaymentResponse("SUCCESS", 1L, 100.0));
        doNothing().when(emailClient).sendEmail(any(EmailRequest.class));

            System.out.println("Initial Order: " + order.getId()+order.getUserEmail());


        EmailRequest emailRequest = new EmailRequest(
                order.getUserEmail(),
                "Order Confirmation - #" + order.getId(),
                "Dear Customer,\n\nYour payment was successful, and your order (ID: " + order.getId() + ") is confirmed!\n\nThank you for choosing us."
        );

        // Act
        Order result = orderService.placeOrder(order);

        // Assert
        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());

        // Verify email was sent
        verify(emailClient, times(1)).sendEmail(emailRequest);
    }

    @Test
    public void testPlaceOrder_FailedPayment() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(new PaymentResponse("FAILED", 1L, 100.0));

        // Act
        Order result = orderService.placeOrder(order);

        // Assert
        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
    }
}
