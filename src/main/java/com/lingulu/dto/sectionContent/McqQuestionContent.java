package com.lingulu.dto.sectionContent;

import com.lingulu.entity.sectionType.MCQQuestion;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class McqQuestionContent {

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
                .build();
    }
}
