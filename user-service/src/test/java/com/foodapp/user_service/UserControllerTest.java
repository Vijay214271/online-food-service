package com.foodapp.user_service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.user_service.controller.UserController;
import com.foodapp.user_service.dto.LoginRequest;
import com.foodapp.user_service.dto.PasswordResetRequest;
import com.foodapp.user_service.dto.PasswordResetTokenRequest;
import com.foodapp.user_service.dto.UserDTO;
import com.foodapp.user_service.model.User;
import com.foodapp.user_service.model.UserRegistrationRequest;
import com.foodapp.user_service.repository.UserRepository;
import com.foodapp.user_service.service.EmailService;
import com.foodapp.user_service.service.JwtUtil;
import com.foodapp.user_service.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    private final static Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock 
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllUsers() throws Exception {
        UserDTO user = new UserDTO("john@example.com","John Doe", "password");
        
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetUserById() throws Exception {
        System.out.println("Mock UserService: " + userService);
        // Set<String> roles = Set.of("ROLE_USER");
        User user = new User(1L, "John Doe", "john@example.com", "password");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateUser() throws Exception {
        User user = new User(2L, "Jane Doe", "jane@example.com", "password");
        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateUser() throws Exception {
        User updatedUser = new User(1L, "Updated User", "updated@example.com", "password");
        when(userService.getUserById(1L)).thenReturn(Optional.of(updatedUser));
        when(userService.updateUser(any(Long.class), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated User"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    // @Test
    // public void testRegisterUser() throws Exception {
    //     Set<String> roles = Set.of("ROLE_USER");
    //     UserRegistrationRequest request = new UserRegistrationRequest("test","test@example.com", "password", roles);
    //     User registeredUser = User.builder()
    //             .id(1L)
    //             .fullName("John Doe")
    //             .email("john@example.com")
    //             .password("password")
    //             .roles(Set.of("ROLE_USER"))
    //             .confirmationToken("some-token")
    //             .build();
    //     when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    //     when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    //     when(userRepository.save(any(User.class))).thenReturn(registeredUser);
    //     doNothing().when(emailService).sendRegistrationEmail(eq("test@example.com"), anyString());

    //     mockMvc.perform(post("/users/register")
    //     .contentType(MediaType.APPLICATION_JSON)
    //     .content(objectMapper.writeValueAsString(request)))
    //     .andExpect(status().isOk()) // Expect HTTP 200 OK
    //     .andExpect(jsonPath("$.id").value(1))
    //     .andExpect(jsonPath("$.fullName").value("John Doe"))
    //     .andExpect(jsonPath("$.email").value("john@example.com"));
    // }

    @Test
    public void testResetPassword() throws Exception {
        String resetToken = "reset-token";
        String newPassword = "newPassword123";

        mockMvc.perform(post("/users/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PasswordResetTokenRequest(resetToken, newPassword))))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successful!"));
    }

    @Test
    public void testForgotPassword() throws Exception {
        String resetEmail = "testuser@example.com";
        when(userService.generateResetToken(resetEmail)).thenReturn("reset-token");

        mockMvc.perform(post("/users/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PasswordResetRequest(resetEmail))))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset email sent!"));
    }


     @Test
    public void testLoginUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("USER_EMAIL");
        loginRequest.setPassword("USER_PASSWORD");

        String mockJwtToken = "mock-jwt-token";

        when(userService.verify(any())).thenReturn(mockJwtToken);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(mockJwtToken));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetCurrentUser() throws Exception {
        String mockToken = "mock-jwt-token";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("Test User");
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("password");
    
        // Assuming you have a method to convert User to UserDTO
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setFullName(mockUser.getFullName());
        mockUserDTO.setEmail(mockUser.getEmail());
    
        when(jwtUtil.extractUserName(mockToken.substring(7))).thenReturn("test@gmail.com");
        when(userService.getUserByFullName("Test User")).thenReturn(Optional.of(mockUserDTO));
        // Mock the return to be Optional<UserDTO>
        when(userService.getUserByEmail("test@gmail.com")).thenReturn(Optional.of(mockUserDTO));
    
        ResultActions resultActions =  mockMvc.perform(get("/users/me").header("Authorization", "Bearer " + mockToken));
        logger.info("Result Action : "+resultActions.andReturn().getResponse().getContentAsString());

                // .andExpect(status().isOk())
                // .andExpect(jsonPath("$.email").value("test@gmail.com"))
                // .andExpect(jsonPath("$.fullName").value("Test User"));
    }


    @Test
    public void testRegisterUser() throws Exception {
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFullName("Test User");
        registrationRequest.setEmail("test@gmail.com");
        registrationRequest.setPassword("password");
        registrationRequest.setRoles(Set.of("USER"));

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("Test User");
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("password");

        when(userService.register(any(), any())).thenReturn(mockUser);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }
    
}
