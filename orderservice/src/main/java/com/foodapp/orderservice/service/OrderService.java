package com.foodapp.orderservice.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodapp.orderservice.client.EmailClient;
import com.foodapp.orderservice.client.PaymentClient;
import com.foodapp.orderservice.dto.EmailRequest;
import com.foodapp.orderservice.dto.OrderRequestDTO;
import com.foodapp.orderservice.dto.OrderResponseDTO;
import com.foodapp.orderservice.dto.PaymentRequest;
import com.foodapp.orderservice.dto.PaymentResponse;
import com.foodapp.orderservice.model.Order;
import com.foodapp.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmailClient emailClient;
    private final PaymentClient paymentClient;

    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order with ID " + id + " not found"));
        
        return convertToResponseDTO(order);
    }

    public OrderResponseDTO createOrder(OrderRequestDTO orderDto) {
        // 1️⃣ Create Order Entity
        Order orderEntity = Order.builder()
            .userEmail(orderDto.getUserEmail())
            .userId(orderDto.getUserId())
            .restaurantId(orderDto.getRestaurantId())
            .totalAmount(orderDto.getTotalAmount())
            .status(orderDto.getStatus())
            .createdAt(orderDto.getCreatedAt())
            .build();
    
        // 2️⃣ Save Order to DB
        Order savedOrder = orderRepository.save(orderEntity);
        log.info("✅ Order created: {}", savedOrder);
    
        // 3️⃣ Send Email Notification
        EmailRequest emailRequest = new EmailRequest(
            savedOrder.getUserEmail(),
            "Order Confirmation - #" + savedOrder.getId(),
            "Dear customer,\n\nYour order has been placed successfully! Your order ID is " + savedOrder.getId() + ".\n\nThank you for choosing us!"
        );
    
        emailClient.sendEmail(emailRequest);
        log.info("📧 Order confirmation email sent to {}", savedOrder.getUserEmail());
    
        // 4️⃣ Return Response DTO
        return convertToResponseDTO(savedOrder);
    }
    
    @Transactional
    public void deleteOrder(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new RuntimeException("Order with ID " + id + " does not exist");
        }
        orderRepository.deleteById(order.get().getId());
        log.info("🗑️ Order with ID {} deleted successfully", id);
    }

    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO updatedOrderRequest) {
        Order existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order with ID " + id + " not found"));
  
        existingOrder.setTotalAmount(updatedOrderRequest.getTotalAmount());
        existingOrder.setStatus(updatedOrderRequest.getStatus());
        log.info("✏️ Order with ID {} after update: TotalAmount={}, Status={}", 
        id, existingOrder.getTotalAmount(), existingOrder.getStatus());
        Order savedOrder = orderRepository.save(existingOrder);
        log.info("✏️ Order with ID {} updated: {}", id, savedOrder);

        return convertToResponseDTO(savedOrder);
    }

    @Transactional
    public Order placeOrder(Order orderRequest){
        Order order = new Order();
        order.setUserEmail(orderRequest.getUserEmail());
        order.setId(orderRequest.getId());
        order.setStatus("PENDING");
        order.setTotalAmount(orderRequest.getTotalAmount());
        orderRepository.save(order);

        PaymentRequest paymentRequest = new PaymentRequest(order.getId(),order.getTotalAmount());
        PaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);

        if (paymentResponse.getStatus().equals("SUCCESS")) { 
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
        
            // 4️⃣ Trigger Email Service
            EmailRequest emailRequest = new EmailRequest(
                order.getUserEmail(),
                "Order Confirmation - #" + order.getId(),
                "Dear Customer,\n\nYour payment was successful, and your order (ID: " + order.getId() + ") is confirmed!\n\nThank you for choosing us."
            );
        
            emailClient.sendEmail(emailRequest); 
            log.info("📧 Order confirmation email sent to {}", order.getUserEmail());
        
        } else { 
            order.setStatus("FAILED");
            orderRepository.save(order);
        }     
        return order;   
    }
   
   
    private OrderResponseDTO convertToResponseDTO(Order order) {
        return new OrderResponseDTO(
            order.getId(),
            order.getUserId(),
            order.getUserEmail(),
            order.getRestaurantId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt()
        );
    }
}
