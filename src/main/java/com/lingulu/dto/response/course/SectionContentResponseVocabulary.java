package com.lingulu.dto.response.course;

import java.util.List;

import com.lingulu.dto.sectionContent.VocabularyContent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectionContentResponseVocabulary extends SectionContentResponse {
    private List<VocabularyContent> vocabularies;
}
