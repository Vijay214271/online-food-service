package com.foodapp.restaurantservice;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.restaurantservice.Service.RestaurantService;
import com.foodapp.restaurantservice.config.JwtUtil;
import com.foodapp.restaurantservice.controller.RestaurantController;
import com.foodapp.restaurantservice.dto.RestaurantDTO;
import com.foodapp.restaurantservice.model.Restaurant;
import com.foodapp.restaurantservice.repository.RestaurantRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Loads application-test.properties
class RestaurantserviceApplicationTests {
    @Autowired
    private MockMvc mockMvc;

	@Mock
	private JwtUtil jwtUtil;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantController restaurantController;

    @Mock
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
		mockMvc = MockMvcBuilders.standaloneSetup(restaurantController).build();
        restaurantRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateRestaurant() throws Exception {
        String jwtToken ="your-valid-jwt-token";
        RestaurantDTO restaurant = new RestaurantDTO(null, "Spicy Bites", "New York", "Indian", 4.5);
        String username = "user";
        // Mock the service call for createRestaurant
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setName("Spicy Bites");
        restaurantDTO.setLocation("New York");
        restaurantDTO.setCuisine("Indian");
        restaurantDTO.setRating(4.5);
        when(restaurantService.createRestaurant(any())).thenReturn(restaurant);
		when(jwtUtil.extractUsername(jwtToken)).thenReturn(username);
        // Mock the service to return the created restaurantDTO
		when(jwtUtil.validateToken(jwtToken)).thenReturn(true);
        mockMvc.perform(post("/restaurants")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurant)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name").value("Spicy Bites"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllRestaurants() throws Exception {
        restaurantRepository.saveAll(List.of(
            new Restaurant(null, "Spicy Bites", "New York", "Indian", 4.5),
            new Restaurant(null, "Ocean Grill", "Los Angeles", "Seafood", 4.2)
        ));

        when(restaurantService.getAllRestaurants()).thenReturn(
            List.of(
                new Restaurant(null, "Spicy Bites", "New York", "Indian", 4.5),
                new Restaurant(null, "Ocean Grill", "Los Angeles", "Seafood", 4.2)
            ).stream().map(res -> new RestaurantDTO(res.getId(), res.getName(), res.getLocation(), res.getCuisine(), res.getRating())).toList()
        );

        mockMvc.perform(get("/restaurants"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("Spicy Bites")))
            .andExpect(jsonPath("$[1].name", is("Ocean Grill")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetRestaurantById() throws Exception {
        Restaurant restaurant = new Restaurant(1L, "Spicy Bites", "New York", "Indian", 4.5);
        
        when(restaurantService.getRestaurantById(any()))
        .thenReturn(new RestaurantDTO(restaurant.getId(),restaurant.getName(), restaurant.getLocation(),restaurant.getCuisine(), restaurant.getRating()));

        mockMvc.perform(get("/restaurants/" + restaurant.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(restaurant.getName()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateRestaurant() throws Exception {
        // Arrange: Create and save a new restaurant to the repository
        Restaurant originalRestaurant = new Restaurant(1L, "Spicy Bites", "New York", "Indian", 4.5);

        // Create an updated RestaurantDTO object with new values
        RestaurantDTO updatedRestaurantDTO = new RestaurantDTO();
        updatedRestaurantDTO.setName("Spicy Bites 2.0");
        updatedRestaurantDTO.setLocation("New York");
        updatedRestaurantDTO.setCuisine("Indian");
        updatedRestaurantDTO.setRating(4.8);

        // Mock the behavior of the restaurantService to return the updated DTO when updating the restaurant
        when(restaurantService.updateRestaurant(anyLong(), any(Restaurant.class)))
            .thenReturn(updatedRestaurantDTO);

        // Act: Perform the PUT request to update the restaurant
        mockMvc.perform(put("/restaurants/" + originalRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRestaurantDTO)))
                .andExpect(status().isOk()) // Assert: Status is OK
                .andExpect(jsonPath("$.name").value("Spicy Bites 2.0")) // Assert: Name is updated
                .andExpect(jsonPath("$.rating").value(4.8)); // Assert: Rating is updated
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant(1L, "Spicy Bites", "New York", "Indian", 4.5);

        when(restaurantService.deleteRestaurant(restaurant.getId())).thenReturn("Restaurant deleted successfully");

        mockMvc.perform(delete("/restaurants/" + restaurant.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value("Restaurant deleted successfully."));
    }
}
