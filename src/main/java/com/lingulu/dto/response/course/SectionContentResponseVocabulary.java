package com.lingulu.dto.response.course;

import java.util.List;

import com.lingulu.dto.sectionContent.VocabularyContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectionContentResponseVocabulary extends SectionContentResponse {
    private List<VocabularyContent> vocabularies;
}
