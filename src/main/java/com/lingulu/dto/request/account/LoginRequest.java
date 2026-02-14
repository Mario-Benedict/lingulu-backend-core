package com.lingulu.dto.request.account;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "Email is empty")
    @Email(message = "Email is not valid")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is empty")
    private String password;

    @NotNull(message = "RememberMe cannot be null")
    private Boolean isRememberMe;
}
