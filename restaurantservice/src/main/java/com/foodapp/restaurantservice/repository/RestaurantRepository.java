package com.foodapp.restaurantservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodapp.restaurantservice.model.Restaurant;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByLocation(String location);
    List<Restaurant> findByCuisine(String cuisine);
}
