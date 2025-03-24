package com.foodapp.deliveryservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.foodapp.deliveryservice.dto.DeliveryRequestDTO;
import com.foodapp.deliveryservice.dto.DeliveryResponseDTO;
import com.foodapp.deliveryservice.service.DeliveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    // Assign a new delivery
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DeliveryResponseDTO> createDelivery(@RequestBody DeliveryRequestDTO deliveryRequestDTO) {
        return ResponseEntity.ok(deliveryService.createDelivery(deliveryRequestDTO));
    }

    // Get delivery by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));  // Fixed issue here
    }

    // Get delivery by Order ID
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getDeliveryByOrderId(orderId));
    }

    // Get all deliveries
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeliveryResponseDTO>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    // Update delivery status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(id, status));
    }

    // Delete delivery by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }

    // Save delivery (Optional: If you want to use it separately)
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponseDTO> saveDelivery(@RequestBody DeliveryRequestDTO request) {
        return ResponseEntity.ok(deliveryService.saveDelivery(request));
    }
}
