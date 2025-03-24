package com.example.API_GATEWAY;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ApiGatewayTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRouteRequestToUserService() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))  // Gateway should forward this to User Service
                .andExpect(status().isOk());
    }

    @Test
    void shouldRouteRequestToOrderService() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/123"))  // Gateway should forward this to Order Service
                .andExpect(status().isOk());
    }
}
