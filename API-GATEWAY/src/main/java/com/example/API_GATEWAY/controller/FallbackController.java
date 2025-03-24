package com.example.API_GATEWAY.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/order")
    public ResponseEntity<String> orderServiceFallback(){
        return ResponseEntity.ok("Order Service is currently unavailable. Please try again later.");
    }
}
