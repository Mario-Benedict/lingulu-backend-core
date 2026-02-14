package com.lingulu.dto.sectionContent;

import com.lingulu.entity.sectionType.MCQQuestion;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McqQuestionContent {

    private UUID questionId;
    private String question;
    private List<McqOptionContent> options;

    public static McqQuestionContent from(MCQQuestion q) {
        return McqQuestionContent.builder()
                .question(q.getQuestionText())
                .options(
                        q.getOptions().stream()
                                .map(McqOptionContent::from)
                                .toList()
                )
                .questionId(q.getQuestionId())
                .build();
    }
}
