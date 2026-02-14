package com.lingulu.dto.request.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpRequest {
    @NotBlank(message = "Email is empty")
    @Email(message = "Email is not valid")
    private String email;
}
