package com.lingulu.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    private String username;

    @NotBlank(message = "Email is empty")
    @Email(message = "Email is not valid")
    private String email;

    @Size(min = 8, message = "Password must be at least 6 characters")
    @NotBlank(message = "Password is empty")
    private String password;
    
    private String confirmPassword;
}
