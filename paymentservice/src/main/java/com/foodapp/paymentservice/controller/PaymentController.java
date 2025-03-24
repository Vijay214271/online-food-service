package com.foodapp.paymentservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.foodapp.paymentservice.dto.PaymentRequestDTO;
import com.foodapp.paymentservice.dto.PaymentResponseDTO;
import com.foodapp.paymentservice.dto.RefundRequestDTO;
import com.foodapp.paymentservice.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/process")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @PostMapping("/refund")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> refundPayment(@RequestBody RefundRequestDTO refundRequest) {
        return ResponseEntity.ok(paymentService.refundRequest(refundRequest));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO responseDTO = paymentService.getPaymentById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(@PathVariable Long id, @RequestBody String status) {
    PaymentResponseDTO responseDTO = paymentService.updatePaymentStatus(id, status);
    return ResponseEntity.ok(responseDTO);
    }
}
