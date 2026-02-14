package com.lingulu.dto.response.course;

import com.lingulu.dto.sectionContent.GrammarContent;
import com.lingulu.dto.sectionContent.McqContent;
import com.lingulu.dto.sectionContent.SpeakingContent;
import com.lingulu.dto.sectionContent.VocabularyContent;
import com.lingulu.enums.SectionType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionContentResponse {

    private UUID sectionId;
    private SectionType sectionType;

    private GrammarContent grammar;
    private List<VocabularyContent> vocabularies;
    private List<SpeakingContent> speakings;
    private McqContent mcq;
}
