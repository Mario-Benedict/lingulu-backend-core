package com.lingulu.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequest {
    @NotBlank(message = "Email is empty")
    @Email(message = "Email is not valid")
    private String email;
}
