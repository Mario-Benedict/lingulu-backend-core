package com.lingulu.dto.response.course;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
    private String questionId;
    private String questionText;
    private String selectedOptionId;
    private String selectedOptionText;
    private Boolean isCorrect;
}
