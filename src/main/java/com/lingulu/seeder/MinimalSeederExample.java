package com.lingulu.seeder;

import com.lingulu.seeder.config.CourseSeederConfig;
import com.lingulu.seeder.config.CourseSeederConfig.*;

import java.util.Arrays;

/**
 * Minimal Example - Seeder configuration dengan data minimal
 * Berguna untuk testing atau development dengan data yang lebih sedikit
 */
public class MinimalSeederExample {

    /**
     * Generate 1 Course dengan 2 Lessons, masing-masing 4 Sections
     * Cocok untuk testing atau development awal
     */
    public static CourseSeederConfig getMinimalConfig() {
        return CourseSeederConfig.builder()
                .courses(Arrays.asList(
                        CourseData.builder()
                                .courseTitle("Test Course - Bahasa Inggris Dasar")
                                .description("Course untuk testing dan development")
                                .difficultyLevel("Beginner")
                                .languageFrom("Indonesian")
                                .languageTo("English")
                                .position(1)
                                .published(true)
                                .lessons(Arrays.asList(
                                        // Lesson 1
                                        LessonData.builder()
                                                .lessonTitle("Greetings")
                                                .position(1)
                                                .sections(Arrays.asList(
                                                        // Vocabulary
                                                        SectionData.builder()
                                                                .sectionTitle("Vocabulary - Greetings")
                                                                .sectionType("VOCABULARY")
                                                                .vocabularies(Arrays.asList(
                                                                        VocabularyData.builder()
                                                                                .word("Hello")
                                                                                .translation("Halo")
                                                                                .audioPath("/audio/hello.mp3")
                                                                                .build(),
                                                                        VocabularyData.builder()
                                                                                .word("Good morning")
                                                                                .translation("Selamat pagi")
                                                                                .audioPath("/audio/good_morning.mp3")
                                                                                .build()
                                                                ))
                                                                .build(),
                                                        // Grammar
                                                        SectionData.builder()
                                                                .sectionTitle("Grammar - Present Simple")
                                                                .sectionType("GRAMMAR")
                                                                .grammar(GrammarData.builder()
                                                                        .title("Present Simple Tense")
                                                                        .filePath("/grammar/present_simple.pdf")
                                                                        .build())
                                                                .build(),
                                                        // Speaking
                                                        SectionData.builder()
                                                                .sectionTitle("Speaking - Practice")
                                                                .sectionType("SPEAKING")
                                                                .speakings(Arrays.asList(
                                                                        SpeakingData.builder()
                                                                                .sentence("Hello, how are you?")
                                                                                .audioPath("/audio/hello_how_are_you.mp3")
                                                                                .build(),
                                                                        SpeakingData.builder()
                                                                                .sentence("I am fine, thank you")
                                                                                .audioPath("/audio/i_am_fine.mp3")
                                                                                .build()
                                                                ))
                                                                .build(),
                                                        // MCQ
                                                        SectionData.builder()
                                                                .sectionTitle("Quiz - Greetings")
                                                                .sectionType("MCQ")
                                                                .mcqQuestions(Arrays.asList(
                                                                        MCQQuestionData.builder()
                                                                                .questionText("What is 'Halo' in English?")
                                                                                .options(Arrays.asList(
                                                                                        MCQOptionData.builder()
                                                                                                .optionText("Hello")
                                                                                                .isCorrect(true)
                                                                                                .build(),
                                                                                        MCQOptionData.builder()
                                                                                                .optionText("Goodbye")
                                                                                                .isCorrect(false)
                                                                                                .build()
                                                                                ))
                                                                                .build()
                                                                ))
                                                                .build()
                                                ))
                                                .build(),
                                        // Lesson 2
                                        LessonData.builder()
                                                .lessonTitle("Numbers")
                                                .position(2)
                                                .sections(Arrays.asList(
                                                        // Vocabulary
                                                        SectionData.builder()
                                                                .sectionTitle("Vocabulary - Numbers")
                                                                .sectionType("VOCABULARY")
                                                                .vocabularies(Arrays.asList(
                                                                        VocabularyData.builder()
                                                                                .word("One")
                                                                                .translation("Satu")
                                                                                .audioPath("/audio/one.mp3")
                                                                                .build(),
                                                                        VocabularyData.builder()
                                                                                .word("Two")
                                                                                .translation("Dua")
                                                                                .audioPath("/audio/two.mp3")
                                                                                .build()
                                                                ))
                                                                .build(),
                                                        // Grammar
                                                        SectionData.builder()
                                                                .sectionTitle("Grammar - Counting")
                                                                .sectionType("GRAMMAR")
                                                                .grammar(GrammarData.builder()
                                                                        .title("Numbers and Counting")
                                                                        .filePath("/grammar/numbers.pdf")
                                                                        .build())
                                                                .build(),
                                                        // Speaking
                                                        SectionData.builder()
                                                                .sectionTitle("Speaking - Count")
                                                                .sectionType("SPEAKING")
                                                                .speakings(Arrays.asList(
                                                                        SpeakingData.builder()
                                                                                .sentence("I have one apple")
                                                                                .audioPath("/audio/one_apple.mp3")
                                                                                .build()
                                                                ))
                                                                .build(),
                                                        // MCQ
                                                        SectionData.builder()
                                                                .sectionTitle("Quiz - Numbers")
                                                                .sectionType("MCQ")
                                                                .mcqQuestions(Arrays.asList(
                                                                        MCQQuestionData.builder()
                                                                                .questionText("What is '1' in English?")
                                                                                .options(Arrays.asList(
                                                                                        MCQOptionData.builder()
                                                                                                .optionText("One")
                                                                                                .isCorrect(true)
                                                                                                .build(),
                                                                                        MCQOptionData.builder()
                                                                                                .optionText("Two")
                                                                                                .isCorrect(false)
                                                                                                .build()
                                                                                ))
                                                                                .build()
                                                                ))
                                                                .build()
                                                ))
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }
}

