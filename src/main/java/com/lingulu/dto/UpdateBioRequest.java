package com.lingulu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateBioRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,!?-]{1,500}$", message = "Bio must be between 1 and 500 characters and contain only alphanumeric characters, spaces, and common punctuation.")   
    private String bio;
}
