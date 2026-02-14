package com.lingulu.dto.sectionContent;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyContent {
    private String word;
    private String translation;
    private String audioPath;
}
