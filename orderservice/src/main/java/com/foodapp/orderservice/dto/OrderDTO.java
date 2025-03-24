package com.foodapp.orderservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private String userEmail;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;

    public OrderDTO(Long id, Long userId, String userEmail, Long restaurantId, Double totalAmount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }
}
