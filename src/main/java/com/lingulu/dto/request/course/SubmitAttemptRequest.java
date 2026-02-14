package com.lingulu.dto.request.course;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitAttemptRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String sectionId;
    
    private List<AnswerRequest> answers;
}
