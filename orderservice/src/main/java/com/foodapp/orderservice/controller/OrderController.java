package com.foodapp.orderservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.foodapp.orderservice.dto.ErrorResponse;
import com.foodapp.orderservice.dto.OrderRequestDTO;
import com.foodapp.orderservice.dto.OrderResponseDTO;
import com.foodapp.orderservice.model.Order;
import com.foodapp.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    @Autowired
    private final OrderService orderService;

    // ✅ Fetch all orders
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ✅ Fetch order by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // ✅ Create new order
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderDto) {
        return ResponseEntity.ok(orderService.createOrder(orderDto));
    }

    // ✅ Update existing order
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long id, @RequestBody OrderRequestDTO updatedOrder) {
        return ResponseEntity.ok(orderService.updateOrder(id, updatedOrder));
    }

    // ✅ Place an order (triggers email)
    @PostMapping("/place")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.placeOrder(order));
    }

    // ✅ Delete order
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Handle exceptions (e.g., Order Not Found)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("❌ Error: {}", ex.getMessage());
    
        // Create an ErrorResponse object with the exception message
        ErrorResponse errorResponse = new ErrorResponse(
            "Order with ID " + ex.getMessage() + " not found", // Error message
            "Order Not Found"  // You can include additional details here
        );
    
        // Return the error response as a JSON object with HTTP status 400 (Bad Request)
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)  // Explicitly set the content type
                .body(errorResponse);
    }
    
    
}
