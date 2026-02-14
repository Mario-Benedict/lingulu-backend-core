package com.lingulu.dto.response.course;

import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingResponse {
    private List<WordResponse> words;
    private String sentence;
    private float averageScore;
}
