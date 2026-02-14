package com.lingulu.seeder.config;

import com.lingulu.seeder.config.CourseSeederConfig.*;

import java.util.Arrays;

/**
 * Example configuration untuk Course Data Seeder
 * Sesuaikan data ini dengan kebutuhan Anda
 */
public class CourseSeederConfigExample {

    /**
     * Contoh konfigurasi: 3 Course, 4 Lessons per Course, 4 Sections per Lesson
     * Section 1: Vocabulary (Material)
     * Section 2: Grammar (Material)
     * Section 3: Speaking (Exercise)
     * Section 4: MCQ (Exercise)
     */
    public static CourseSeederConfig getExampleConfig() {
        return CourseSeederConfig.builder()
                .courses(Arrays.asList(
                        createCourse1(),
                        createCourse2(),
                        createCourse3()
                ))
                .build();
    }

    private static CourseData createCourse1() {
        return CourseData.builder()
                .courseTitle("Bahasa Inggris Dasar")
                .description("Kursus bahasa Inggris untuk pemula")
                .difficultyLevel("Beginner")
                .languageFrom("Indonesian")
                .languageTo("English")
                .position(1)
                .published(true)
                .lessons(Arrays.asList(
                        createLesson("Perkenalan", 1),
                        createLesson("Keluarga", 2),
                        createLesson("Aktivitas Sehari-hari", 3),
                        createLesson("Makanan dan Minuman", 4)
                ))
                .build();
    }

    private static CourseData createCourse2() {
        return CourseData.builder()
                .courseTitle("Bahasa Inggris Menengah")
                .description("Kursus bahasa Inggris untuk tingkat menengah")
                .difficultyLevel("Intermediate")
                .languageFrom("Indonesian")
                .languageTo("English")
                .position(2)
                .published(true)
                .lessons(Arrays.asList(
                        createLesson("Traveling", 1),
                        createLesson("Shopping", 2),
                        createLesson("Kesehatan", 3),
                        createLesson("Pekerjaan", 4)
                ))
                .build();
    }

    private static CourseData createCourse3() {
        return CourseData.builder()
                .courseTitle("Bahasa Inggris Lanjutan")
                .description("Kursus bahasa Inggris untuk tingkat lanjutan")
                .difficultyLevel("Advanced")
                .languageFrom("Indonesian")
                .languageTo("English")
                .position(3)
                .published(true)
                .lessons(Arrays.asList(
                        createLesson("Business English", 1),
                        createLesson("Academic Writing", 2),
                        createLesson("Public Speaking", 3),
                        createLesson("Idioms and Expressions", 4)
                ))
                .build();
    }

    private static LessonData createLesson(String title, int position) {
        return LessonData.builder()
                .lessonTitle(title)
                .position(position)
                .sections(Arrays.asList(
                        createVocabularySection(title),
                        createGrammarSection(title),
                        createSpeakingSection(title),
                        createMCQSection(title)
                ))
                .build();
    }

    // Section 1: Vocabulary (Material)
    private static SectionData createVocabularySection(String lessonTitle) {
        return SectionData.builder()
                .sectionTitle("Kosakata " + lessonTitle)
                .sectionType("VOCABULARY")
                .vocabularies(Arrays.asList(
                        VocabularyData.builder()
                                .word("Hello")
                                .translation("Halo")
                                .audioPath("/audio/vocab/hello.mp3")
                                .build(),
                        VocabularyData.builder()
                                .word("Goodbye")
                                .translation("Selamat tinggal")
                                .audioPath("/audio/vocab/goodbye.mp3")
                                .build(),
                        VocabularyData.builder()
                                .word("Thank you")
                                .translation("Terima kasih")
                                .audioPath("/audio/vocab/thankyou.mp3")
                                .build(),
                        VocabularyData.builder()
                                .word("Please")
                                .translation("Tolong")
                                .audioPath("/audio/vocab/please.mp3")
                                .build(),
                        VocabularyData.builder()
                                .word("Sorry")
                                .translation("Maaf")
                                .audioPath("/audio/vocab/sorry.mp3")
                                .build()
                ))
                .build();
    }

    // Section 2: Grammar (Material)
    private static SectionData createGrammarSection(String lessonTitle) {
        return SectionData.builder()
                .sectionTitle("Tata Bahasa " + lessonTitle)
                .sectionType("GRAMMAR")
                .grammar(GrammarData.builder()
                        .title("Present Tense - " + lessonTitle)
                        .filePath("/grammar/" + lessonTitle.toLowerCase().replace(" ", "_") + ".pdf")
                        .build())
                .build();
    }

    // Section 3: Speaking (Exercise)
    private static SectionData createSpeakingSection(String lessonTitle) {
        return SectionData.builder()
                .sectionTitle("Latihan Berbicara " + lessonTitle)
                .sectionType("SPEAKING")
                .speakings(Arrays.asList(
                        SpeakingData.builder()
                                .sentence("Hello, how are you?")
                                .audioPath("/audio/speaking/hello_how_are_you.mp3")
                                .build(),
                        SpeakingData.builder()
                                .sentence("My name is John")
                                .audioPath("/audio/speaking/my_name_is_john.mp3")
                                .build(),
                        SpeakingData.builder()
                                .sentence("Nice to meet you")
                                .audioPath("/audio/speaking/nice_to_meet_you.mp3")
                                .build(),
                        SpeakingData.builder()
                                .sentence("Where are you from?")
                                .audioPath("/audio/speaking/where_are_you_from.mp3")
                                .build()
                ))
                .build();
    }

    // Section 4: MCQ (Exercise)
    private static SectionData createMCQSection(String lessonTitle) {
        return SectionData.builder()
                .sectionTitle("Kuis " + lessonTitle)
                .sectionType("MCQ")
                .mcqQuestions(Arrays.asList(
                        MCQQuestionData.builder()
                                .questionText("What is the correct translation of 'Hello'?")
                                .options(Arrays.asList(
                                        MCQOptionData.builder()
                                                .optionText("Halo")
                                                .isCorrect(true)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Selamat tinggal")
                                                .isCorrect(false)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Terima kasih")
                                                .isCorrect(false)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Maaf")
                                                .isCorrect(false)
                                                .build()
                                ))
                                .build(),
                        MCQQuestionData.builder()
                                .questionText("Which one is a greeting?")
                                .options(Arrays.asList(
                                        MCQOptionData.builder()
                                                .optionText("Goodbye")
                                                .isCorrect(false)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Hello")
                                                .isCorrect(true)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Sorry")
                                                .isCorrect(false)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Please")
                                                .isCorrect(false)
                                                .build()
                                ))
                                .build(),
                        MCQQuestionData.builder()
                                .questionText("What does 'Thank you' mean?")
                                .options(Arrays.asList(
                                        MCQOptionData.builder()
                                                .optionText("Halo")
                                                .isCorrect(false)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Maaf")
                                                .isCorrect(false)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Terima kasih")
                                                .isCorrect(true)
                                                .build(),
                                        MCQOptionData.builder()
                                                .optionText("Tolong")
                                                .isCorrect(false)
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }
}


