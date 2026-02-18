package com.lingulu.dto.request.course;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakingRequest {
    @NotNull(message = "Words cannot be null")
    private List<WordRequest> words;

    @NotBlank
    private String sectionId;

    @NotBlank
    private String sentence;

    @NotNull(message = "Score cannot be null")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot exceed 100")
    private float averageScore;
}
