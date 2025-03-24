package com.foodapp.paymentservice.dto;

import lombok.Data;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private Double totalAmount;
    private String orderStatus;
}
