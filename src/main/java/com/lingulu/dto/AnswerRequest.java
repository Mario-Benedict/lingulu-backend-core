package com.lingulu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String questionId;
    
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String selectedOptionId;
}
