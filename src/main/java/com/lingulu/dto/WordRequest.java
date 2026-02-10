package com.lingulu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WordRequest {
    @NotBlank
    private String word;

    @NotBlank
    private float score;
}
