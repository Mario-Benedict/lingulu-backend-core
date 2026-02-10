package com.lingulu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnsweredQuestion {
    private String questionId;
    private String selectedOptionId;
    private Boolean isCorrect;
}
