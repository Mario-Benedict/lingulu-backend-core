package com.lingulu.service;

import com.lingulu.dto.response.course.SectionContentResponse;
import com.lingulu.dto.response.course.SectionContentResponseGrammar;
import com.lingulu.dto.response.course.SectionContentResponseMcqContent;
import com.lingulu.dto.response.course.SectionContentResponseSpeaking;
import com.lingulu.dto.response.course.SectionContentResponseVocabulary;
import com.lingulu.dto.sectionContent.GrammarContent;
import com.lingulu.dto.sectionContent.McqContent;
import com.lingulu.dto.sectionContent.SpeakingContent;
import com.lingulu.dto.sectionContent.VocabularyContent;
import com.lingulu.entity.course.Section;
import com.lingulu.entity.course.SectionGrammar;
import com.lingulu.entity.course.SectionMCQQuestion;
import com.lingulu.entity.course.SectionSpeaking;
import com.lingulu.entity.course.SectionVocabulary;
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

        // SectionContentResponse.SectionContentResponseBuilder builder =
        //         SectionContentResponse.builder()
        //                 .sectionId(section.getSectionId())
        //                 .sectionTitle(section.getSectionTitle())
        //                 .sectionType(section.getSectionType());

        // switch (section.getSectionType()) {

        //     case GRAMMAR -> builder.grammar(
        //             GrammarContent.builder()
        //                     .title(section.getGrammar().getTitle())
        //                     .filePath(section.getGrammar().getFilePath())
        //                     .build()
        //     );

        //     case VOCABULARY -> builder.vocabularies(
        //             section.getVocabularies().stream()
        //                     .map(v -> VocabularyContent.builder()
        //                             .word(v.getWord())
        //                             .translation(v.getTranslation())
        //                             .audioPath(v.getAudioPath())
        //                             .build())
        //                     .toList()
        //     );

        //     case SPEAKING -> builder.speakings(
        //             section.getSpeakings().stream()
        //                     .map(s -> SpeakingContent.builder()
        //                             .sentence(s.getSentence())
        //                             .audioPath(s.getAudioPath())
        //                             .build())
        //                     .toList()
        //     );

        //     case MCQ -> builder.mcq(
        //             McqContent.from(section.getMcqQuestions())
        //     );
        // }

        // return builder.build();

        if(section instanceof SectionGrammar grammar) {
                SectionContentResponseGrammar sectionContentResponseGrammar = new SectionContentResponseGrammar();
                sectionContentResponseGrammar.setGrammar(grammar.getGrammars().stream()
                                                .map(g -> GrammarContent.builder()
                                                        .title(g.getTitle())
                                                        .filePath(g.getFilePath())
                                                        .build())
                                                .toList());
                sectionContentResponseGrammar.setSectionId(grammar.getSectionId());
                sectionContentResponseGrammar.setSectionTitle(grammar.getSectionTitle());
                sectionContentResponseGrammar.setSectionType(grammar.getSectionType());

                return sectionContentResponseGrammar;
        }
        else if(section instanceof SectionMCQQuestion mcq) {
                SectionContentResponseMcqContent mcqContent = new SectionContentResponseMcqContent();
                mcqContent.setSectionId(mcq.getSectionId());
                mcqContent.setSectionTitle(mcq.getSectionTitle());
                mcqContent.setSectionType(mcq.getSectionType());

                mcqContent.setMcq(McqContent.from(mcq.getMcqQuestions()));

                return mcqContent;
        }
        else if(section instanceof SectionVocabulary vocab) {
                SectionContentResponseVocabulary vocabularyContent = new SectionContentResponseVocabulary();
                vocabularyContent.setSectionId(vocab.getSectionId());
                vocabularyContent.setSectionTitle(vocab.getSectionTitle());
                vocabularyContent.setSectionType(vocab.getSectionType());

                vocabularyContent.setVocabularies(vocab.getVocabularies().stream()
                                        .map(v -> VocabularyContent.builder()
                                                .word(v.getWord())
                                                .vocabId(v.getVocabId())
                                                .translation(v.getTranslation())
                                                .audioPath(v.getVocabAudioPath())
                                                .build())
                                                .toList());
                return vocabularyContent;

        }
        else if(section instanceof SectionSpeaking speaking) {
                SectionContentResponseSpeaking speakingContent = new SectionContentResponseSpeaking();
                speakingContent.setSectionId(speaking.getSectionId());
                speakingContent.setSectionTitle(speaking.getSectionTitle());
                speakingContent.setSectionType(speaking.getSectionType());
                speakingContent.setSpeakings(speaking.getSpeakings().stream()
                                        .map(s -> SpeakingContent.builder()
                                                .speakingId(s.getExerciseId())
                                                .sentence(s.getSentence())
                                                .audioPath(s.getSpeakingAudioPath())
                                                .build())
                                        .toList());
                return speakingContent;
        }

        return null;
    }
}
