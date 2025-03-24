package com.foodapp.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodapp.user_service.model.User;



public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findUserById(long id);
    Optional<User> findByFullName(String fullName);
    Optional<User> findByConfirmationToken(String token);
}
