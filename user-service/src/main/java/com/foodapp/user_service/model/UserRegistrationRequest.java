package com.foodapp.user_service.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {
    private String fullName;
    private String email;
    private String password;
    private Set<String> roles;

    public User toUser(){
        return User.builder()
        .fullName(this.fullName)
        .email(this.email)
        .password(this.password)  // Encrypt this password in service
        .roles(this.roles)
        .build();
    }
}
