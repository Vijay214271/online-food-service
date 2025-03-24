package com.foodapp.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.foodapp.orderservice.dto.EmailRequest;

@FeignClient(name = "EMAIL-SERVICE")
public interface EmailClient {
    @PostMapping("api/email/send")
    void sendEmail(@RequestBody EmailRequest emailRequest);
}
