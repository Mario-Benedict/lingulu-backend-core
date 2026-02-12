package com.lingulu.entity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnsweredQuestion {
    private String questionId;
    private String selectedOptionId;
    private Boolean isCorrect;
}
