package com.foodapp.paymentservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private Long orderId;
    private Double amount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paymentDate;

    @JsonProperty("paymentStatus")
    public String getPaymentStatus() {
        return paymentStatus;
    }
}
