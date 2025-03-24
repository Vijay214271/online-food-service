package com.foodapp.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private Long userId;
    private String userEmail;
    private Long restaurantId;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
}

