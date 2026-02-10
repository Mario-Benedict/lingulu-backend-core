package com.lingulu.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerResponse {
    private String questionId;
    private String questionText;
    private String selectedOptionId;
    private String selectedOptionText;
    private Boolean isCorrect;
}
