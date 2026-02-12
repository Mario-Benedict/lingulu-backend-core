package com.lingulu.dto.request.account;

import com.lingulu.validator.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@PasswordMatch(first = "password", second = "confirmPassword", message = "Confirm Password does not match Password")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Username is empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is empty")
    @Email(message = "Email is not valid")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is empty")
    private String password;

    @NotBlank(message = "Confirm Password is empty")
    @Size(min = 8, message = "Confirm password must be at least 8 characters")
    private String confirmPassword;
}
