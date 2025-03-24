package com.foodapp.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

import com.foodapp.orderservice.dto.PaymentRequest;
import com.foodapp.orderservice.dto.PaymentResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {
    @RequestMapping(method = RequestMethod.POST, value = "/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
}
