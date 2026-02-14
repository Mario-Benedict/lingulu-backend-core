package com.lingulu.dto.response.course;

import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResponse {
    private String sectionId;
    private List<AnswerResponse> answers;
    private int totalQuestions;
    private int correctAnswers;
    private int score;
}
