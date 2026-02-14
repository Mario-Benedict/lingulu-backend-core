package com.lingulu.seeder.config;

import com.lingulu.seeder.config.CourseSeederConfig.*;

import java.util.Arrays;

/**
 * Template untuk custom seeder configuration
 * Copy file ini dan rename sesuai kebutuhan, lalu modifikasi data-nya
 */
public class CourseSeederConfigTemplate {

    /**
     * TEMPLATE: Ganti dengan data Anda sendiri
     * Struktur: 3 Course -> 4 Lessons each -> 4 Sections each
     */
    public static CourseSeederConfig getCustomConfig() {
        return CourseSeederConfig.builder()
                .courses(Arrays.asList(
                        buildCourse1(),
                        buildCourse2(),
                        buildCourse3()
                ))
                .build();
    }

    // ============================================================================
    // COURSE DEFINITIONS
    // ============================================================================

    private static CourseData buildCourse1() {
        return CourseData.builder()
                .courseTitle("YOUR COURSE TITLE 1")
                .description("Your course description here")
                .difficultyLevel("Beginner") // Beginner, Intermediate, Advanced
                .languageFrom("Indonesian")
                .languageTo("English")
                .position(1)
                .published(true)
                .lessons(Arrays.asList(
                        buildLesson(1, "Lesson 1 Title", "Topic 1"),
                        buildLesson(2, "Lesson 2 Title", "Topic 2"),
                        buildLesson(3, "Lesson 3 Title", "Topic 3"),
                        buildLesson(4, "Lesson 4 Title", "Topic 4")
                ))
                .build();
    }

    private static CourseData buildCourse2() {
        return CourseData.builder()
                .courseTitle("YOUR COURSE TITLE 2")
                .description("Your course description here")
                .difficultyLevel("Intermediate")
                .languageFrom("Indonesian")
                .languageTo("English")
                .position(2)
                .published(true)
                .lessons(Arrays.asList(
                        buildLesson(1, "Lesson 1 Title", "Topic 1"),
                        buildLesson(2, "Lesson 2 Title", "Topic 2"),
                        buildLesson(3, "Lesson 3 Title", "Topic 3"),
                        buildLesson(4, "Lesson 4 Title", "Topic 4")
                ))
                .build();
    }

    private static CourseData buildCourse3() {
        return CourseData.builder()
                .courseTitle("YOUR COURSE TITLE 3")
                .description("Your course description here")
                .difficultyLevel("Advanced")
                .languageFrom("Indonesian")
                .languageTo("English")
                .position(3)
                .published(true)
                .lessons(Arrays.asList(
                        buildLesson(1, "Lesson 1 Title", "Topic 1"),
                        buildLesson(2, "Lesson 2 Title", "Topic 2"),
                        buildLesson(3, "Lesson 3 Title", "Topic 3"),
                        buildLesson(4, "Lesson 4 Title", "Topic 4")
                ))
                .build();
    }

    // ============================================================================
    // LESSON BUILDER
    // ============================================================================

    private static LessonData buildLesson(int position, String title, String topic) {
        return LessonData.builder()
                .lessonTitle(title)
                .position(position)
                .sections(Arrays.asList(
                        buildVocabularySection(topic),
                        buildGrammarSection(topic),
                        buildSpeakingSection(topic),
                        buildMCQSection(topic)
                ))
                .build();
    }

    // ============================================================================
    // SECTION BUILDERS - Customize your content here
    // ============================================================================

    /**
     * VOCABULARY SECTION (Material)
     * Add your vocabulary items here
     */
    private static SectionData buildVocabularySection(String topic) {
        return SectionData.builder()
                .sectionTitle("Vocabulary: " + topic)
                .sectionType("VOCABULARY")
                .vocabularies(Arrays.asList(
                        // Add more vocabulary items as needed
                        VocabularyData.builder()
                                .word("Word 1")
                                .translation("Terjemahan 1")
                                .audioPath("/audio/vocab/word1.mp3")
                                .build(),
                        VocabularyData.builder()
                                .word("Word 2")
                                .translation("Terjemahan 2")
                                .audioPath("/audio/vocab/word2.mp3")
                                .build(),
                        VocabularyData.builder()
                                .word("Word 3")
                                .translation("Terjemahan 3")
                                .audioPath("/audio/vocab/word3.mp3")
                                .build()
                ))
                .build();
    }

    /**
     * GRAMMAR SECTION (Material)
     * Add your grammar material here
     */
    private static SectionData buildGrammarSection(String topic) {
        return SectionData.builder()
                .sectionTitle("Grammar: " + topic)
                .sectionType("GRAMMAR")
                .grammar(GrammarData.builder()
                        .title("Grammar Title - " + topic)
                        .filePath("/grammar/" + topic.toLowerCase().replace(" ", "_") + ".pdf")
                        .build())
                .build();
    }

    /**
     * SPEAKING SECTION (Exercise)
     * Add your speaking exercises here
     */
    private static SectionData buildSpeakingSection(String topic) {
        return SectionData.builder()
                .sectionTitle("Speaking Practice: " + topic)
                .sectionType("SPEAKING")
                .speakings(Arrays.asList(
                        // Add more speaking exercises as needed
                        SpeakingData.builder()
                                .sentence("Example sentence 1")
                                .audioPath("/audio/speaking/sentence1.mp3")
                                .build(),
                        SpeakingData.builder()
                                .sentence("Example sentence 2")
                                .audioPath("/audio/speaking/sentence2.mp3")
                                .build(),
                        SpeakingData.builder()
                                .sentence("Example sentence 3")
                                .audioPath("/audio/speaking/sentence3.mp3")
                                .build()
                ))
                .build();
    }

    /**
     * MCQ SECTION (Exercise)
     * Add your multiple choice questions here
     */
    private static SectionData buildMCQSection(String topic) {
        return SectionData.builder()
                .sectionTitle("Quiz: " + topic)
                .sectionType("MCQ")
                .mcqQuestions(Arrays.asList(
                        // Question 1
                        MCQQuestionData.builder()
                                .questionText("Question 1: Your question here?")
                                .options(Arrays.asList(
                                        MCQOptionData.builder()
                                                .optionText("Option A")
                                                .isCorrect(true) // Set true untuk jawaban benar
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Option B")
                                                .isCorrect(false)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Option C")
                                                .isCorrect(false)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Option D")
                                                .isCorrect(false)
                                                .build()
                                ))
                                .build(),
                        // Question 2
                        MCQQuestionData.builder()
                                .questionText("Question 2: Your question here?")
                                .options(Arrays.asList(
                                        MCQOptionData.builder()
                                                .optionText("Option A")
                                                .isCorrect(false)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Option B")
                                                .isCorrect(true)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Option C")
                                                .isCorrect(false)
                                                .build()
                                ))
                                .build(),
                        // Add more questions as needed
                        MCQQuestionData.builder()
                                .questionText("Question 3: Your question here?")
                                .options(Arrays.asList(
                                        MCQOptionData.builder()
                                                .optionText("True")
                                                .isCorrect(true)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("False")
                                                .isCorrect(false)
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    // ============================================================================
    // HELPER METHODS (Optional)
    // ============================================================================

    /**
     * Helper untuk membuat vocabulary dengan format yang sama
     */
    private static VocabularyData vocab(String word, String translation, String audioFileName) {
        return VocabularyData.builder()
                .word(word)
                .translation(translation)
                .audioPath("/audio/vocab/" + audioFileName)
                .build();
    }

    /**
     * Helper untuk membuat speaking exercise
     */
    private static SpeakingData speaking(String sentence, String audioFileName) {
        return SpeakingData.builder()
                .sentence(sentence)
                .audioPath("/audio/speaking/" + audioFileName)
                .build();
    }

    /**
     * Helper untuk membuat MCQ option
     */
    private static MCQOptionData option(String text, boolean correct) {
        return MCQOptionData.builder()
                .optionText(text)
                .isCorrect(correct)
                .build();
    }
}

