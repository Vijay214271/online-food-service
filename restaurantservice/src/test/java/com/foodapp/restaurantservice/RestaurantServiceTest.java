package com.foodapp.restaurantservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.foodapp.restaurantservice.Service.RestaurantService;
import com.foodapp.restaurantservice.dto.RestaurantDTO;
import com.foodapp.restaurantservice.model.Restaurant;
import com.foodapp.restaurantservice.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant mockRestaurant;

    @BeforeEach
    void setUp() {
        mockRestaurant = new Restaurant(1L, "Spicy Bites", "Hyderabad", "Indian", 4.5);
        
    }

    @Test
    void testFindRestaurantById() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(mockRestaurant));

        RestaurantDTO foundRestaurant = restaurantService.getRestaurantById(1L);

        assertNotNull(foundRestaurant);
        assertEquals("Spicy Bites", foundRestaurant.getName());
        assertEquals("Hyderabad", foundRestaurant.getLocation());
        assertEquals(4.5, foundRestaurant.getRating());
        verify(restaurantRepository, times(1)).findById(1L);
    }

    @Test
    void testFindRestaurantById_NotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> restaurantService.getRestaurantById(99L));
    }

    @Test
    void testSaveRestaurant() {
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(mockRestaurant);

        Restaurant savedRestaurant = restaurantService.saveRestaurant(mockRestaurant);

        assertNotNull(savedRestaurant);
        assertEquals("Spicy Bites", savedRestaurant.getName());
        verify(restaurantRepository, times(1)).save(mockRestaurant);
    }

    @Test
    void testUpdateRestaurant() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(mockRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(mockRestaurant);

        mockRestaurant.setName("New Spicy Bites");
        mockRestaurant.setRating(4.8);

        RestaurantDTO updatedRestaurant = restaurantService.updateRestaurant(1L, mockRestaurant);

        assertNotNull(updatedRestaurant);
        assertEquals("New Spicy Bites", updatedRestaurant.getName());
        assertEquals(4.8, updatedRestaurant.getRating());
        verify(restaurantRepository, times(1)).save(mockRestaurant);
    }

    @Test
    void testDeleteRestaurant() {

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(mockRestaurant));
        doNothing().when(restaurantRepository).deleteById(1L);

        assertDoesNotThrow(() -> restaurantService.deleteRestaurant(1L));

        verify(restaurantRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetAllRestaurants() {
        List<Restaurant> restaurantList = Arrays.asList(
            new Restaurant(1L, "Spicy Bites", "Hyderabad", "Indian", 4.5),
            new Restaurant(2L, "Pizza Palace", "Bangalore", "Italian", 4.2)
        );

        when(restaurantRepository.findAll()).thenReturn(restaurantList);

        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();

        assertEquals(2, restaurants.size());
        verify(restaurantRepository, times(1)).findAll();
    }
}
