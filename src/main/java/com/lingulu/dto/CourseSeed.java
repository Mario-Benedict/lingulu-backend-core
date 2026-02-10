package com.lingulu.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseSeed {

    private String courseTitle;
    private String description;
    private String level;
    private Integer position;
    private List<LessonSeed> lessons;

    @Data
    public static class LessonSeed {
        private String lessonTitle;
        private Integer position;
        private List<SectionSeed> sections;
    }

    @Data
    public static class SectionSeed {
        private String sectionType;

        // VOCABULARY
        private List<VocabularySeed> vocabulary;

        // GRAMMAR
        private String sectionTitle;
        private String grammarPath;

        // SPEAKING
        private List<SpeakingSeed> sentences;

        // MCQ
        private List<QuestionSeed> questions;
    }

    @Data
    public static class VocabularySeed {
        private String word;
        private String translation;
        private String audiopath;
    }

    @Data
    public static class SpeakingSeed {
        private String sentence;
        private String audioPath;
    }

    @Data
    public static class QuestionSeed {
        private String question;
        private List<OptionSeed> options;
    }

    @Data
    public static class OptionSeed {
        private String text;
        private boolean isTrue;
    }
}
