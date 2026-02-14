package com.lingulu.dto.request.course;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakingRequest {
    @NotBlank
    private List<WordRequest> words;

    @NotBlank
    private String sectionId;

    @NotBlank
    private String sentence;
    
    @NotBlank
    private float averageScore;
}
