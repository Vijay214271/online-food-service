package com.foodapp.deliveryservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequestDTO {
    private Long orderId;
    private Long deliveryPersonId;
    private LocalDateTime estimatedDeliveryTime;
}
