package com.foodapp.restaurantservice.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.restaurantservice.dto.RestaurantDTO;
import com.foodapp.restaurantservice.model.Restaurant;
import com.foodapp.restaurantservice.repository.RestaurantRepository;
import com.foodapp.restaurantservice.exception.RestaurantNotFoundException;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    // Get all restaurants
    public List<RestaurantDTO> getAllRestaurants(){
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get a restaurant by ID
    public RestaurantDTO getRestaurantById(Long id){
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));
        return convertToDTO(restaurant);
    }

    // Create a new restaurant
    public RestaurantDTO createRestaurant(RestaurantDTO restaurantDto) {
        Restaurant restaurant = convertToEntity(restaurantDto);
        return convertToDTO(restaurantRepository.save(restaurant));    
    }

    // Update an existing restaurant
    public RestaurantDTO updateRestaurant(Long id, Restaurant restaurantDto) {
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));
        
        restaurant.setName(restaurantDto.getName());
        restaurant.setLocation(restaurantDto.getLocation());
        restaurant.setCuisine(restaurantDto.getCuisine());
        restaurant.setRating(restaurantDto.getRating());
        
        return convertToDTO(restaurantRepository.save(restaurant));
    }

    // Delete a restaurant by ID
    public String deleteRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));
        
        restaurantRepository.deleteById(restaurant.getId());
        return "Restaurant deleted successfully";
    }

    // Save a restaurant (used for unit tests or internal logic)
    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    // Convert entity to DTO
    private RestaurantDTO convertToDTO(Restaurant restaurant) {
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setId(restaurant.getId());
        restaurantDTO.setName(restaurant.getName());
        restaurantDTO.setLocation(restaurant.getLocation());
        restaurantDTO.setCuisine(restaurant.getCuisine());
        restaurantDTO.setRating(restaurant.getRating());
        return restaurantDTO;
    }

    // Convert DTO to entity
    private Restaurant convertToEntity(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDTO.getName());
        restaurant.setLocation(restaurantDTO.getLocation());
        restaurant.setCuisine(restaurantDTO.getCuisine());
        restaurant.setRating(restaurantDTO.getRating());
        return restaurant;
    }
}
