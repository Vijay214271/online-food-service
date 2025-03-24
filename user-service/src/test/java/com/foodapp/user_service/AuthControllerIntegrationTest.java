// package com.foodapp.user_service;

// import com.foodapp.user_service.controller.AuthController;
// import com.foodapp.user_service.dto.LoginRequest;
// import com.foodapp.user_service.dto.PasswordResetRequest;
// import com.foodapp.user_service.dto.PasswordResetTokenRequest;
// import com.foodapp.user_service.dto.UserDTO;
// import com.foodapp.user_service.model.User;
// import com.foodapp.user_service.model.UserRegistrationRequest;
// import com.foodapp.user_service.repository.PasswordResetTokenRepository;
// import com.foodapp.user_service.repository.UserRepository;
// import com.foodapp.user_service.service.UserService;
// import com.foodapp.user_service.service.EmailService;
// import com.foodapp.user_service.service.JwtUtil;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import java.util.List;
// import java.util.Optional;
// import java.util.Set;

// @SpringBootTest
// @AutoConfigureMockMvc
// public class AuthControllerIntegrationTest {

//     @Mock 
//     private AuthenticationManager authenticationManager;

//     @Autowired
//     private MockMvc mockMvc;

//     @Mock
//     private UserService userService;

//     @Mock
//     private EmailService emailService;

//     @Mock
//     private JwtUtil jwtUtil;

//     @Mock
//     private PasswordResetTokenRepository passwordResetTokenRepository;

//     @Mock
//     private UserRepository userRepository;

//     @InjectMocks
//     private AuthController authController;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private static final String USER_EMAIL = "testuser@example.com";
//     private static final String USER_PASSWORD = "testPassword123";

//     @BeforeEach
//     public void setup() {

//         User mockUser = new User();
//     mockUser.setEmail(USER_EMAIL);
//     mockUser.setPassword(USER_PASSWORD);

//     when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(mockUser));

//         // Setup mock responses for services if necessary
//         when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//         .thenReturn(new UsernamePasswordAuthenticationToken(USER_EMAIL,null,List.of(new SimpleGrantedAuthority("ROLE_USER"))));
//     }

//     // @Test
//     // public void testRegisterUser() throws Exception {
//     //     UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
//     //     registrationRequest.setFullName("Test User");
//     //     registrationRequest.setEmail(USER_EMAIL);
//     //     registrationRequest.setPassword(USER_PASSWORD);
//     //     registrationRequest.setRoles(Set.of("USER"));

//     //     User mockUser = new User();
//     //     mockUser.setId(1L);
//     //     mockUser.setFullName("Test User");
//     //     mockUser.setEmail(USER_EMAIL);
//     //     mockUser.setPassword(USER_PASSWORD);

//     //     when(userService.register(any(), any())).thenReturn(mockUser);

//     //     mockMvc.perform(post("/api/auth/register")
//     //                     .contentType(MediaType.APPLICATION_JSON)
//     //                     .content(objectMapper.writeValueAsString(registrationRequest)))
//     //             .andExpect(status().isOk())
//     //             .andExpect(jsonPath("$.email").value(USER_EMAIL));
//     // }

//     // @Test
//     // public void testLoginUser() throws Exception {
//     //     LoginRequest loginRequest = new LoginRequest();
//     //     loginRequest.setUsername(USER_EMAIL);
//     //     loginRequest.setPassword(USER_PASSWORD);
//     //     String mockJwtToken = "mock-jwt-token";
//     //     when(userService.verify(any())).thenReturn(mockJwtToken);
//     //     mockMvc.perform(post("/api/auth/login")
//     //                     .contentType(MediaType.APPLICATION_JSON)
//     //                     .content(objectMapper.writeValueAsString(loginRequest)))
//     //             .andExpect(status().isOk())
//     //             .andExpect(jsonPath("$").value(mockJwtToken));
//     // }

//     // @Test
//     // public void testForgotPassword() throws Exception {
//     //     String resetEmail = "testuser@example.com";
//     //     when(userService.generateResetToken(resetEmail)).thenReturn("reset-token");
//     //     mockMvc.perform(post("/api/auth/forgot-password")
//     //                     .contentType(MediaType.APPLICATION_JSON)
//     //                     .content(objectMapper.writeValueAsString(new PasswordResetRequest(resetEmail))))
//     //             .andExpect(status().isOk())
//     //             .andExpect(content().string("Password reset email sent!"));
//     // }

//     // @Test
//     // @WithMockUser(roles = {"USER"})
//     // public void testGetCurrentUser() throws Exception {
//     //     String mockToken = "mock-jwt-token";
//     //     User mockUser = new User();
//     //     mockUser.setId(1L);
//     //     mockUser.setFullName("Test User");
//     //     mockUser.setEmail(USER_EMAIL);
//     //     mockUser.setPassword(USER_PASSWORD);
    
//     //     // Assuming you have a method to convert User to UserDTO
//     //     UserDTO mockUserDTO = new UserDTO();
//     //     mockUserDTO.setFullName(mockUser.getFullName());
//     //     mockUserDTO.setEmail(mockUser.getEmail());
    
//     //     when(jwtUtil.extractUserName(mockToken.substring(7))).thenReturn(USER_EMAIL);
        
//     //     // Mock the return to be Optional<UserDTO>
//     //     when(userService.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(mockUserDTO));
    
//     //     mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + mockToken))
//     //             .andExpect(status().isOk())
//     //             .andExpect(jsonPath("$.email").value(USER_EMAIL))
//     //             .andExpect(jsonPath("$.fullName").value("Test User"));
//     // }

//     // @Test
//     // public void testResetPassword() throws Exception {
//     //     String resetToken = "reset-token";
//     //     String newPassword = "newPassword123";

//     //     mockMvc.perform(post("/api/auth/reset-password")
//     //                     .contentType(MediaType.APPLICATION_JSON)
//     //                     .content(objectMapper.writeValueAsString(new PasswordResetTokenRequest(resetToken, newPassword))))
//     //             .andExpect(status().isOk())
//     //             .andExpect(content().string("Password reset successful!"));
//     // }
// }
