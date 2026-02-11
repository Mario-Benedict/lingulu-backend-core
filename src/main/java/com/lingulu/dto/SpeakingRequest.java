package com.lingulu.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
