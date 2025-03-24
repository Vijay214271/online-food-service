package com.foodapp.user_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.foodapp.user_service.dto.LoginRequest;
import com.foodapp.user_service.exception.UserNotFoundException;
import com.foodapp.user_service.model.PasswordResetToken;
import com.foodapp.user_service.model.User;
import com.foodapp.user_service.repository.PasswordResetTokenRepository;
import com.foodapp.user_service.repository.UserRepository;
import com.foodapp.user_service.service.JwtUtil;
import com.foodapp.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, tokenRepository, passwordEncoder, jwtUtil);
        userService.authenticationManager = authenticationManager;  // Set the authentication manager
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setFullName("User One");
        
        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setFullName("User Two");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        var users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("test1@example.com", users.get(0).getEmail());
        assertEquals("User One", users.get(0).getFullName());
    }

    @Test
    void testGetUserById_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals("Test User", result.get().getFullName());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testSaveUser_UserAlreadyExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.saveUser(user));
    }

    @Test
    void testSaveUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        var result = userService.saveUser(user);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testVerifyConfirmationToken() {
        String token = "sampleToken";
        User user = new User();
        user.setConfirmationToken(token);
        user.setConfirmed(false);

        when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(user));

        boolean result = userService.verifyConfirmationToken(token);

        assertTrue(result);
        assertTrue(user.isConfirmed());
        assertNull(user.getConfirmationToken());
    }

    @Test
    void testGenerateResetToken() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String token = userService.generateResetToken("test@example.com");

        assertNotNull(token);
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    void testResetPassword_Success() {
        String token = "sampleToken";
        String newPassword = "newPassword";
        PasswordResetToken resetToken = new PasswordResetToken(token, new User());
        resetToken.getUser().setPassword("oldPassword");

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        userService.resetPassword(token, newPassword);

        assertEquals("encodedNewPassword", resetToken.getUser().getPassword());
        verify(tokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void testResetPassword_InvalidToken() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.resetPassword("invalidToken", "newPassword"));
    }

    @Test
    void testVerifyLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test@example.com");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtUtil.generateToken(request)).thenReturn("jwtToken");

        String token = userService.verify(request);

        assertEquals("jwtToken", token);
    }

    @Test
    void testVerifyLogin_Failure() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test@example.com");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        String token = userService.verify(request);

        assertEquals("Login Failed", token);
    }
}
