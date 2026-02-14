package com.lingulu.seeder;

import com.lingulu.entity.course.*;
import com.lingulu.entity.sectionType.*;
import com.lingulu.enums.SectionType;
import com.lingulu.repository.*;
import com.lingulu.repository.sections.MCQOptionRepository;
import com.lingulu.repository.sections.MCQQuestionRepository;
import com.lingulu.seeder.config.CourseSeederConfig;
import com.lingulu.seeder.config.CourseSeederConfig.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Course Data Seeder
 * Untuk generate data Course dengan Lessons dan Sections (Vocabulary, Grammar, Speaking, MCQ)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourseDataSeeder {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    private final VocabularyRepository vocabularyRepository;
    private final GrammarRepository grammarRepository;
    private final SpeakingRepository speakingRepository;
    private final MCQQuestionRepository mcqQuestionRepository;
    private final MCQOptionRepository mcqOptionRepository;

    /**
     * Seed data berdasarkan configuration
     * @param config CourseSeederConfig yang berisi data yang akan di-seed
     */
    @Transactional
    public void seedData(CourseSeederConfig config) {
        log.info("Starting course data seeding...");

        if (config == null || config.getCourses() == null || config.getCourses().isEmpty()) {
            log.warn("No course data to seed");
            return;
        }

        for (CourseData courseData : config.getCourses()) {
            seedCourse(courseData);
        }

        log.info("Course data seeding completed!");
    }

    /**
     * Seed individual course
     */
    private void seedCourse(CourseData courseData) {
        log.info("Seeding course: {}", courseData.getCourseTitle());

        // Create Course
        Course course = Course.builder()
                .courseTitle(courseData.getCourseTitle())
                .description(courseData.getDescription())
                .difficultyLevel(courseData.getDifficultyLevel())
                .languageFrom(courseData.getLanguageFrom())
                .languageTo(courseData.getLanguageTo())
                .position(courseData.getPosition())
                .published(courseData.isPublished())
                .lessons(new ArrayList<>())
                .build();

        course = courseRepository.save(course);
        log.info("Created course: {} with ID: {}", course.getCourseTitle(), course.getCourseId());

        // Create Lessons
        if (courseData.getLessons() != null && !courseData.getLessons().isEmpty()) {
            for (LessonData lessonData : courseData.getLessons()) {
                seedLesson(course, lessonData);
            }
        }
    }

    /**
     * Seed individual lesson
     */
    private void seedLesson(Course course, LessonData lessonData) {
        log.info("  Seeding lesson: {}", lessonData.getLessonTitle());

        Lesson lesson = Lesson.builder()
                .lessonTitle(lessonData.getLessonTitle())
                .position(lessonData.getPosition())
                .course(course)
                .sections(new ArrayList<>())
                .build();

        lesson = lessonRepository.save(lesson);
        log.info("  Created lesson: {} with ID: {}", lesson.getLessonTitle(), lesson.getLessonId());

        // Create Sections
        if (lessonData.getSections() != null && !lessonData.getSections().isEmpty()) {
            for (SectionData sectionData : lessonData.getSections()) {
                seedSection(lesson, sectionData);
            }
        }
    }

    /**
     * Seed individual section
     */
    private void seedSection(Lesson lesson, SectionData sectionData) {
        log.info("    Seeding section: {} (Type: {})", sectionData.getSectionTitle(), sectionData.getSectionType());

        SectionType sectionType = SectionType.valueOf(sectionData.getSectionType().toUpperCase());

        Section section = Section.builder()
                .sectionTitle(sectionData.getSectionTitle())
                .sectionType(sectionType)
                .lesson(lesson)
                .build();

        section = sectionRepository.save(section);
        log.info("    Created section: {} with ID: {}", section.getSectionTitle(), section.getSectionId());

        // Seed section content based on type
        switch (sectionType) {
            case VOCABULARY:
                seedVocabularies(section, sectionData.getVocabularies());
                break;
            case GRAMMAR:
                seedGrammar(section, sectionData.getGrammar());
                break;
            case SPEAKING:
                seedSpeakings(section, sectionData.getSpeakings());
                break;
            case MCQ:
                seedMCQQuestions(section, sectionData.getMcqQuestions());
                break;
        }
    }

    /**
     * Seed vocabularies for a section
     */
    private void seedVocabularies(Section section, List<VocabularyData> vocabularies) {
        if (vocabularies == null || vocabularies.isEmpty()) {
            log.warn("      No vocabulary data for section: {}", section.getSectionTitle());
            return;
        }

        for (VocabularyData vocabData : vocabularies) {
            Vocabulary vocabulary = Vocabulary.builder()
                    .section(section)
                    .word(vocabData.getWord())
                    .translation(vocabData.getTranslation())
                    .audioPath(vocabData.getAudioPath())
                    .build();

            vocabularyRepository.save(vocabulary);
            log.info("      Created vocabulary: {} -> {}", vocabData.getWord(), vocabData.getTranslation());
        }
    }

    /**
     * Seed grammar for a section
     */
    private void seedGrammar(Section section, GrammarData grammarData) {
        if (grammarData == null) {
            log.warn("      No grammar data for section: {}", section.getSectionTitle());
            return;
        }

        Grammar grammar = Grammar.builder()
                .sectionId(section.getSectionId())
                .section(section)
                .title(grammarData.getTitle())
                .filePath(grammarData.getFilePath())
                .build();

        grammarRepository.save(grammar);
        log.info("      Created grammar: {}", grammarData.getTitle());
    }

    /**
     * Seed speaking exercises for a section
     */
    private void seedSpeakings(Section section, List<SpeakingData> speakings) {
        if (speakings == null || speakings.isEmpty()) {
            log.warn("      No speaking data for section: {}", section.getSectionTitle());
            return;
        }

        for (SpeakingData speakingData : speakings) {
            Speaking speaking = Speaking.builder()
                    .section(section)
                    .sentence(speakingData.getSentence())
                    .audioPath(speakingData.getAudioPath())
                    .build();

            speakingRepository.save(speaking);
            log.info("      Created speaking exercise: {}", speakingData.getSentence());
        }
    }

    /**
     * Seed MCQ questions for a section
     */
    private void seedMCQQuestions(Section section, List<MCQQuestionData> mcqQuestions) {
        if (mcqQuestions == null || mcqQuestions.isEmpty()) {
            log.warn("      No MCQ data for section: {}", section.getSectionTitle());
            return;
        }

        for (MCQQuestionData questionData : mcqQuestions) {
            MCQQuestion question = MCQQuestion.builder()
                    .section(section)
                    .questionText(questionData.getQuestionText())
                    .options(new ArrayList<>())
                    .build();

            question = mcqQuestionRepository.save(question);
            log.info("      Created MCQ question: {}", questionData.getQuestionText());

            // Create options
            if (questionData.getOptions() != null && !questionData.getOptions().isEmpty()) {
                for (MCQOptionData optionData : questionData.getOptions()) {
                    MCQOption option = MCQOption.builder()
                            .question(question)
                            .optionText(optionData.getOptionText())
                            .isCorrect(optionData.getIsCorrect())
                            .build();

                    mcqOptionRepository.save(option);
                    log.info("        Created option: {} (Correct: {})",
                            optionData.getOptionText(), optionData.getIsCorrect());
                }
            }
        }
    }

    /**
     * Clear all course data
     * WARNING: This will delete all courses and related data!
     */
    @Transactional
    public void clearAllData() {
        log.warn("Clearing all course data...");
        courseRepository.deleteAll();
        log.info("All course data cleared!");
    }

    /**
     * Check if data already exists
     */
    public boolean hasData() {
        return courseRepository.count() > 0;
    }
}

