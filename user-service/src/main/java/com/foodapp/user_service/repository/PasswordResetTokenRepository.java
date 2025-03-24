package com.foodapp.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodapp.user_service.model.PasswordResetToken;


public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByToken(String token);
    
}
