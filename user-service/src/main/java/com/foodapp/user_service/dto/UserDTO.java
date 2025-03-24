package com.foodapp.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserDTO {
    private String email;
    private String fullName;
    private String password;
}
