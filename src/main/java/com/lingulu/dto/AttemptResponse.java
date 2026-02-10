package com.lingulu.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttemptResponse {
    private String sectionId;
    private List<AnswerResponse> answers;
    private int totalQuestions;
    private int correctAnswers;
    private int score;
}
