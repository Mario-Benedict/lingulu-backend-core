package com.lingulu.seeder.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Configuration class untuk data seeder
 * Struktur: Course -> Lessons -> Sections (Vocabulary, Grammar, Speaking, MCQ)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSeederConfig {

    private List<CourseData> courses;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseData {
        private String courseTitle;
        private String description;
        private String difficultyLevel;
        private String languageFrom;
        private String languageTo;
        private Integer position;
        private boolean published;
        private List<LessonData> lessons;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonData {
        private String lessonTitle;
        private Integer position;
        private List<SectionData> sections;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionData {
        private String sectionTitle;
        private String sectionType; // VOCABULARY, GRAMMAR, SPEAKING, MCQ

        // Data untuk VOCABULARY section
        private List<VocabularyData> vocabularies;

        // Data untuk GRAMMAR section
        private GrammarData grammar;

        // Data untuk SPEAKING section
        private List<SpeakingData> speakings;

        // Data untuk MCQ section
        private List<MCQQuestionData> mcqQuestions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VocabularyData {
        private String word;
        private String translation;
        private String audioPath;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarData {
        private String title;
        private String filePath;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpeakingData {
        private String sentence;
        private String audioPath;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MCQQuestionData {
        private String questionText;
        private List<MCQOptionData> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MCQOptionData {
        private String optionText;
        private Boolean isCorrect;
    }
}

