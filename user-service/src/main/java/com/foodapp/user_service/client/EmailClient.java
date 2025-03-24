package com.foodapp.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.foodapp.user_service.dto.EmailRequest;

@FeignClient(name = "EMAIL-SERVICE")
public interface EmailClient {
@PostMapping("api/email/send")
    void sendEmail(@RequestBody EmailRequest emailRequest);
}
