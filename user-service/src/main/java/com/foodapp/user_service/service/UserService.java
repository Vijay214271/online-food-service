package com.foodapp.user_service.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodapp.user_service.dto.LoginRequest;
import com.foodapp.user_service.dto.UserDTO;
import com.foodapp.user_service.exception.UserNotFoundException;
import com.foodapp.user_service.model.PasswordResetToken;
import com.foodapp.user_service.model.User;
import com.foodapp.user_service.repository.PasswordResetTokenRepository;
import com.foodapp.user_service.repository.UserRepository;

@Service
public class UserService {

    private final JwtUtil jwtUtil;
    public AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public UserService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = null;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setEmail(user.getEmail());
            dto.setFullName(user.getFullName());
            return dto;
        }).toList();
    }

    public Optional<User> getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));

        User dto = new User();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPassword(user.getPassword());
        return Optional.of(dto);
    }

    public Optional<UserDTO> getUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with Email " + email));

        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        return Optional.of(dto);
    }

    public Optional<UserDTO> getUserByFullName(String username) {
        User user = userRepository.findByFullName(username).orElseThrow(()-> new UserNotFoundException("User Not Found with Full name" + username));

        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        return Optional.of(dto);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));

        if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getFullName() != null) existingUser.setFullName(updatedUser.getFullName());
        if (updatedUser.getPassword() != null) existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        return userRepository.save(existingUser);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            tokenRepository.deleteById(id);
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User not found with ID " + id);
        }
    }

    public User saveUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with email already exists");
        } else {
            user.setPassword(user.getPassword() != null ? passwordEncoder.encode(user.getPassword()) : null);
            return userRepository.save(user);
        }
    }

    public String generateConfirmationToken(){
        return UUID.randomUUID().toString();
    }

    public User register(User user,Set<String> roles) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with email already exists");
        }
        if (roles == null || roles.isEmpty()) {
        user.setRoles(new HashSet<>(Collections.singleton("USER")));  // Default role is USER
        } else {
        user.setRoles(roles);  // Assign provided roles (could include "ADMIN")
        }
        String confirmationToken = generateConfirmationToken();
        user.setConfirmationToken(confirmationToken);

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        userRepository.save(user);
        return user;
    }

    public boolean verifyConfirmationToken(String token) {
        User user = userRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid confirmation token"));
        
        // You can set the user as confirmed if needed (e.g. set a field like "isConfirmed")
        user.setConfirmed(true);
        user.setConfirmationToken(null); // Optional: clear the confirmation token after successful confirmation
        userRepository.save(user);
    
        return true;
    }
    
    public String verify(LoginRequest request){
        Authentication authenticate;
        authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if(authenticate.isAuthenticated()){
            return jwtUtil.generateToken(request);
        }
        return "Login Failed";
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public String generateResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);
        return token;
    }

    // ✅ Reset password
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
                if (resetToken.isExpired()) {
                    throw new RuntimeException("Password reset token has expired.");
                }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken); // Remove token after use
    }
}
