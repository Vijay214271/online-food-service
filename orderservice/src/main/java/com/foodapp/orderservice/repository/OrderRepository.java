package com.foodapp.orderservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foodapp.orderservice.model.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
