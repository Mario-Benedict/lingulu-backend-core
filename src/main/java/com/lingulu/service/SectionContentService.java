package com.lingulu.service;

import com.lingulu.dto.SectionContentResponse;
import com.lingulu.dto.sectionContent.GrammarContent;
import com.lingulu.dto.sectionContent.McqContent;
import com.lingulu.dto.sectionContent.SpeakingContent;
import com.lingulu.dto.sectionContent.VocabularyContent;
import com.lingulu.entity.Section;
import com.lingulu.exception.DataNotFoundException;
import com.lingulu.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectionContentService {

    private final SectionRepository sectionRepository;

    public SectionContentResponse getSectionContent(UUID sectionId) {

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() ->
                        new DataNotFoundException("Section not found", HttpStatus.NOT_FOUND)
                );

        SectionContentResponse.SectionContentResponseBuilder builder =
                SectionContentResponse.builder()
                        .sectionId(section.getSectionId())
                        .sectionType(section.getSectionType());

        switch (section.getSectionType()) {

            case GRAMMAR -> builder.grammar(
                    GrammarContent.builder()
                            .title(section.getGrammar().getTitle())
                            .description(section.getGrammar().getDescription())
                            .build()
            );

            case VOCABULARY -> builder.vocabularies(
                    section.getVocabularies().stream()
                            .map(v -> VocabularyContent.builder()
                                    .word(v.getWord())
                                    .translation(v.getTranslation())
                                    .audioPath(v.getAudioPath())
                                    .build())
                            .toList()
            );

            case SPEAKING -> builder.speakings(
                    section.getSpeakings().stream()
                            .map(s -> SpeakingContent.builder()
                                    .sentence(s.getSentence())
                                    .audioPath(s.getAudioPath())
                                    .build())
                            .toList()
            );

            case MCQ -> builder.mcq(
                    McqContent.from(section.getMcqQuestions())
            );
        }

        return builder.build();
    }
}
