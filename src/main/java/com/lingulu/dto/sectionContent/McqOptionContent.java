package com.lingulu.dto.sectionContent;

import java.util.UUID;

import com.lingulu.entity.sectionType.MCQOption;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McqOptionContent {
    private UUID optionId;
    private String text;

    public static McqOptionContent from(MCQOption option) {
        return McqOptionContent.builder()
                .text(option.getOptionText())
                .optionId(option.getOptionId())
                .build();
    }
}
