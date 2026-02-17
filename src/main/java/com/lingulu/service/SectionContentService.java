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
import com.lingulu.entity.course.*;
import com.lingulu.enums.ProgressStatus;
import com.lingulu.exception.DataNotFoundException;
import com.lingulu.repository.SectionProgressRepository;
import com.lingulu.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectionContentService {

    private final SectionRepository sectionRepository;
    private final SectionProgressRepository sectionProgressRepository;
    private final CloudFrontSigner cloudFrontSigner;

    public SectionContentResponse getSectionContent(UUID sectionId) {
        String userId = (String) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() ->
                        new DataNotFoundException("Section not found", HttpStatus.NOT_FOUND)
                );

        SectionProgress sectionProgress = sectionProgressRepository.getSectionProgressBysectionId(UUID.fromString(userId), sectionId);

        switch (section) {
            case SectionGrammar grammar -> {
                SectionContentResponseGrammar sectionContentResponseGrammar = new SectionContentResponseGrammar();
                sectionContentResponseGrammar.setGrammar(grammar.getGrammars().stream()
                        .map(g -> GrammarContent.builder()
                                .grammarId(g.getGrammarId())
                                .title(g.getTitle())
                                .filePath(cloudFrontSigner.generateSignedUrl(g.getFilePath()))
                                .build())
                        .toList());
                sectionContentResponseGrammar.setSectionId(grammar.getSectionId());
                sectionContentResponseGrammar.setSectionTitle(grammar.getSectionTitle());
                sectionContentResponseGrammar.setSectionType(grammar.getSectionType());
                sectionContentResponseGrammar.setIsCompleted(sectionProgress.getStatus() == ProgressStatus.COMPLETED);

                return sectionContentResponseGrammar;
            }
            case SectionMCQQuestion mcq -> {
                SectionContentResponseMcqContent mcqContent = new SectionContentResponseMcqContent();
                mcqContent.setSectionId(mcq.getSectionId());
                mcqContent.setSectionTitle(mcq.getSectionTitle());
                mcqContent.setSectionType(mcq.getSectionType());
                mcqContent.setIsCompleted(sectionProgress.getStatus() == ProgressStatus.COMPLETED);

                mcqContent.setMcq(McqContent.from(mcq.getMcqQuestions()));

                return mcqContent;
            }
            case SectionVocabulary vocab -> {
                SectionContentResponseVocabulary vocabularyContent = new SectionContentResponseVocabulary();
                vocabularyContent.setSectionId(vocab.getSectionId());
                vocabularyContent.setSectionTitle(vocab.getSectionTitle());
                vocabularyContent.setSectionType(vocab.getSectionType());
                vocabularyContent.setIsCompleted(sectionProgress.getStatus() == ProgressStatus.COMPLETED);

                vocabularyContent.setVocabularies(vocab.getVocabularies().stream()
                        .map(v -> VocabularyContent.builder()
                                .word(v.getWord())
                                .vocabId(v.getVocabId())
                                .translation(v.getTranslation())
                                .audioPath(cloudFrontSigner.generateSignedUrl(v.getVocabAudioPath()))
                                .build())
                        .toList());
                return vocabularyContent;
            }
            case SectionSpeaking speaking -> {
                SectionContentResponseSpeaking speakingContent = new SectionContentResponseSpeaking();
                speakingContent.setSectionId(speaking.getSectionId());
                speakingContent.setSectionTitle(speaking.getSectionTitle());
                speakingContent.setSectionType(speaking.getSectionType());
                speakingContent.setIsCompleted(sectionProgress.getStatus() == ProgressStatus.COMPLETED);
                speakingContent.setSpeakings(speaking.getSpeakings().stream()
                        .map(s -> SpeakingContent.builder()
                                .speakingId(s.getExerciseId())
                                .sentence(s.getSentence())
                                .audioPath(cloudFrontSigner.generateSignedUrl(s.getSpeakingAudioPath()))
                                .build())
                        .toList());
                return speakingContent;
            }
            default -> {
            }
        }

        return null;
    }
}
