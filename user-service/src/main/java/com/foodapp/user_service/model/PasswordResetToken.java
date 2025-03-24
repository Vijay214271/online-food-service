package com.foodapp.user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private Date expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = new Date(System.currentTimeMillis() + 1000*16*15); // Expires in 1 hour
    }
    public boolean isExpired(){
        return new Date().after(expiryDate);
    }
    public PasswordResetToken() {
    }
}
