package com.lingulu.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmitAttemptRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String sectionId;
    
    private List<AnswerRequest> answers;
}
