package com.foodapp.emailservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.emailservice.dto.EmailRequestDTO;
import com.foodapp.emailservice.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequest) {
        emailService.sendEmail(emailRequest);
        return ResponseEntity.ok("Email sent successfully");
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> getEmailLogs(@PathVariable String id) {
        return ResponseEntity.ok("Email log for ID: " + id);
    }
}
