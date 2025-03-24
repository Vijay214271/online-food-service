package com.foodapp.user_service.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
}
