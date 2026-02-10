package com.lingulu.dto;

import java.util.List;

import jakarta.annotation.Generated;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpeakingResponse {
    private List<WordResponse> words;
    private String sentence;
    private float averageScore;
}
