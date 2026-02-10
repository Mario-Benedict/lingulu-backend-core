package com.lingulu.dto;

import com.lingulu.validator.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@PasswordMatch(first = "password", second = "confirmPassword", message = "Confirm Password does not match Password")
@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    @Size(min = 8, message = "Confirm password must be at least 8 characters")
    private String confirmPassword;
}
