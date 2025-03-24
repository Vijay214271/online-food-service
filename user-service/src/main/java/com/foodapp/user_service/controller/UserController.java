package com.foodapp.user_service.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.user_service.dto.LoginRequest;
import com.foodapp.user_service.dto.PasswordResetRequest;
import com.foodapp.user_service.dto.PasswordResetTokenRequest;
import com.foodapp.user_service.dto.UserDTO;
import com.foodapp.user_service.model.User;
import com.foodapp.user_service.model.UserRegistrationRequest;
import com.foodapp.user_service.service.EmailService;
import com.foodapp.user_service.service.JwtUtil;
import com.foodapp.user_service.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;

    UserController(UserService userService,EmailService emailService,JwtUtil jwtUtil) {
        this.userService = userService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserDTO> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch (Exception e) {
            logger.error("Error retrieving users: {}", e.getMessage());
            throw new RuntimeException("Error retrieving users");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            logger.warn("User with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        Optional<UserDTO> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> {
            logger.warn("User with email {} not found", email);
            return ResponseEntity.notFound().build();
        });

    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            logger.info("User created with ID {}", createdUser.getId());
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            throw new RuntimeException("Error creating user");
        }

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> currentUser = userService.getUserById(id);
        if (currentUser.isPresent()) {
            // Ensure users can only update their own details unless they are admins
            if (currentUser.get().getId().equals(updatedUser.getId()) || currentUser.get().getRoles().contains("ROLE_ADMIN")) {
                User updated = userService.updateUser(id, updatedUser);
                return ResponseEntity.ok(updated);
            } else {
                logger.warn("User attempted to update another user's details");
                return ResponseEntity.status(403).body(null); // Forbidden
            }
        } else {
            logger.warn("User with ID {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
    }

// @PostMapping("/register")
// public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest user) {
//     try {
//         Set<String> roles = user.getRoles();
//         // Register user and send confirmation email
//         User registeredUser = userService.register(user.toUser(),roles);

//         // Create a confirmation link with the token
//         String confirmationLink = "https://localhost:3000/confirm?token=" + registeredUser.getConfirmationToken();

//         // Send registration email with the confirmation link
//         emailService.sendRegistrationEmail(user.getEmail(), confirmationLink);

//         // Log the successful registration
//         logger.info("User registered: {}", registeredUser.getEmail());

//         // Return the registered user as a response
//         return ResponseEntity.ok(registeredUser);
//     } catch (Exception e) {
//         // Log any errors that occur during registration
//         logger.error("Error registering user: {}", e.getMessage());
//         return ResponseEntity.status(500).body(null);
//     }
// }

@GetMapping("/confirm")
public ResponseEntity<String> confirmEmail(@RequestParam String token) {
    boolean isValid = userService.verifyConfirmationToken(token);
    if (isValid) {
        return ResponseEntity.ok("Email confirmed successfully!");
    } else {
        return ResponseEntity.status(400).body("Invalid or expired token.");
    }
}


@PostMapping("/generate-reset-token/{email}")
public ResponseEntity<String> generateResetToken(@PathVariable String email) {
        try {
            String token = userService.generateResetToken(email);
            emailService.sendPasswordResetEmail(email, token);
            logger.info("Password reset token generated for email: {}", email);
            return ResponseEntity.ok("Password reset token sent to email");
        } catch (Exception e) {
            logger.error("Error generating reset token: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error generating reset token");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            logger.info("User with ID {} deleted successfully", id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Error deleting user");
        }
    }

     @PostMapping("/reset-password")
    // @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetTokenRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successful!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequest request) {
        String resetToken = userService.generateResetToken(request.getEmail());
        emailService.sendPasswordResetEmail(request.getEmail(), resetToken);
        return ResponseEntity.ok("Password reset email sent!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(userService.verify(request));
    }

    @GetMapping("/me")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public ResponseEntity<String> getCurrentUser(@RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
        token = token.substring(7);  // Remove "Bearer " prefix
    }

    // Log the token and extracted username
    logger.info("Received token: " + token);
    String username = jwtUtil.extractUserName(token);
    logger.info("Extracted username: " + username);

    if (username == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }

    return ResponseEntity.ok(username);  // Return username for debugging purposes
}



    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest userRequest) {
        try {
            // Pass the roles from the request
            Set<String> roles = userRequest.getRoles();  // roles could be ["USER", "ADMIN"]
            
            // Register user and send confirmation email
            User registeredUser = userService.register(userRequest.toUser(), roles);
            String confirmationLink = "https://localhosst:8082/confirm?token=" + registeredUser.getConfirmationToken();
            emailService.sendRegistrationEmail(userRequest.getEmail(), confirmationLink);
            
            logger.info("User registered: {}", registeredUser.getEmail());
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.status(500).body(null); // Internal server error
        }
    }
}