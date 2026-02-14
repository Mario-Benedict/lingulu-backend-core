package com.lingulu.dto.sectionContent;

import com.lingulu.entity.sectionType.MCQQuestion;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McqContent {
    private List<McqQuestionContent> questions;

    public static McqContent from(List<MCQQuestion> questions) {
        return McqContent.builder()
                .questions(
                        questions.stream()
                                .map(McqQuestionContent::from)
                                .toList()
                )
                .build();
    }
}
