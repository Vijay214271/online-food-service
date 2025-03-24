package com.foodapp.deliveryservice.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodapp.deliveryservice.model.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
}
