// package com.foodapp.user_service.controller;

// import com.foodapp.user_service.dto.*;
// import com.foodapp.user_service.model.User;
// import com.foodapp.user_service.model.UserRegistrationRequest;
// import com.foodapp.user_service.service.EmailService;
// import com.foodapp.user_service.service.UserService;

// import lombok.RequiredArgsConstructor;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;

// import java.util.Optional;
// import java.util.Set;

// @RestController
// @RequestMapping("/api/auth")
// @RequiredArgsConstructor
// public class AuthController {
//     private final com.foodapp.user_service.service.JwtUtil jwtUtil;
//     private final UserService userService;
//     private final EmailService emailService;
//       private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    

//     // // ✅ Get Current User
//     // @GetMapping("/me")
//     // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
//     // public ResponseEntity<Optional<UserDTO>> getCurrentUser(@RequestHeader("Authorization") String token) {
//     //     if (token.startsWith("Bearer ")) {
//     //         token = token.substring(7);
//     //     }
//     //     String username = jwtUtil.extractUserName(token);
//     //     Optional<UserDTO> user = userService.getUserByEmail(username);
//     //     return ResponseEntity.ok(user);
//     // }

//     // // ✅ Register New User
//     // @PostMapping("/register")
//     // public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest userRequest) {
//     //     try {
//     //         // Pass the roles from the request
//     //         Set<String> roles = userRequest.getRoles();  // roles could be ["USER", "ADMIN"]
            
//     //         // Register user and send confirmation email
//     //         User registeredUser = userService.register(userRequest.toUser(), roles);
//     //         String confirmationLink = "https://localhosst:8082/confirm?token=" + registeredUser.getConfirmationToken();
//     //         emailService.sendRegistrationEmail(userRequest.getEmail(), confirmationLink);
            
//     //         logger.info("User registered: {}", registeredUser.getEmail());
//     //         return ResponseEntity.ok(registeredUser);
//     //     } catch (Exception e) {
//     //         logger.error("Error registering user: {}", e.getMessage());
//     //         return ResponseEntity.status(500).body(null); // Internal server error
//     //     }
//     // }


//     // ✅ Login User
//     // @PostMapping("/login")
//     // public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
//     //     return ResponseEntity.ok(userService.verify(request));
//     // }
    
//     // @PostMapping("/forgot-password")
//     // public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequest request) {
//     //     String resetToken = userService.generateResetToken(request.getEmail());
//     //     emailService.sendPasswordResetEmail(request.getEmail(), resetToken);
//     //     return ResponseEntity.ok("Password reset email sent!");
//     // }

//     // ✅ 2. Reset password
//     // @PostMapping("/reset-password")
//     // // @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
//     // public ResponseEntity<String> resetPassword(@RequestBody PasswordResetTokenRequest request) {
//     //     userService.resetPassword(request.getToken(), request.getNewPassword());
//     //     return ResponseEntity.ok("Password reset successful!");
//     // }
// }
