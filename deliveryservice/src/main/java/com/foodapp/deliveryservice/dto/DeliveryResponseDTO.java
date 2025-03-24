package com.foodapp.deliveryservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class DeliveryResponseDTO {
    private Long id;
     private Long orderId;
    private Long deliveryPersonId;
    private String status;
    private LocalDateTime estimatedDeliveryTime;
}
