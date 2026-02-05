package com.lingulu.dto.sectionContent;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabularyContent {
    private String word;
    private String translation;
    private String audioPath;
}
