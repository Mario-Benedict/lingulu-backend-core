package com.lingulu.dto.sectionContent;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyContent {
    private UUID vocabId;
    private String word;
    private String translation;
    private String audioPath;
}
