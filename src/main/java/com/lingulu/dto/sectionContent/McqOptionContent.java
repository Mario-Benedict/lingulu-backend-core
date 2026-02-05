package com.lingulu.dto.sectionContent;

import com.lingulu.entity.sectionType.MCQOption;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class McqOptionContent {
    private String text;

    public static McqOptionContent from(MCQOption option) {
        return McqOptionContent.builder()
                .text(option.getOptionText())
                .build();
    }
}
