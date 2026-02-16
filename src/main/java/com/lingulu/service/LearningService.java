package com.lingulu.service;

import com.lingulu.dto.request.course.SpeakingRequest;
import com.lingulu.dto.response.course.SpeakingResponse;
import com.lingulu.dto.response.course.WordResponse;
import com.lingulu.dto.response.course.AnswerResponse;
import com.lingulu.dto.response.course.AttemptResponse;
import com.lingulu.dto.request.course.SubmitAttemptRequest;
import com.lingulu.entity.course.*;
import com.lingulu.enums.ProgressStatus;
import com.lingulu.exception.DataNotFoundException;
import com.lingulu.repository.*;
import com.lingulu.repository.sections.MCQOptionRepository;
import com.lingulu.repository.sections.MCQQuestionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LearningService {

    private final SectionProgressRepository sectionProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseProgressRepository courseProgressRepository;

    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final LeaderboardService leaderboardService;
    private final UserLearningStatsService userLearningStatsService;
    private final MCQOptionRepository mCQOptionRepository;
    private final MCQQuestionRepository mcqQuestionRepository;

    private final CourseRepository courseRepository;
    private final SpeakingAnswerRepository speakingAnswerRepository;
    private final SpeakingRepository speakingRepository;
    private final MCQAnswerRepository mcqAnswerRepository;

    public void markSectionCompleted(UUID userId, UUID sectionId) {

        SectionProgress sp = sectionProgressRepository.findByUser_UserIdAndSection_SectionId(userId, sectionId)
                                .orElseThrow(() -> new DataNotFoundException("Section progress not found", HttpStatus.BAD_REQUEST));

        if (sp.getStatus() == ProgressStatus.COMPLETED) return;

        sp.setStatus(ProgressStatus.COMPLETED);
        sp.setCompletedAt(LocalDateTime.now());
        sectionProgressRepository.save(sp);

        recalculateLessonProgress(userId, sp.getSection().getLesson());
        leaderboardService.updateTotalPoints(userId);
        userLearningStatsService.updateStreak(userId);
    }

    private void recalculateLessonProgress(UUID userId, Lesson lesson) {
        LessonProgress lp = lessonProgressRepository
                .findByUser_UserIdAndLesson_LessonId(userId, lesson.getLessonId())
                .orElseThrow(() -> new DataNotFoundException("Lesson progress not found", HttpStatus.NOT_FOUND));

        int totalSections = lp.getTotalSections();
        int completedSections = lp.getCompletedSections() + 1;

        lp.setCompletedSections(completedSections);
        lp.setStatus(
                completedSections == totalSections ? ProgressStatus.COMPLETED :
                        completedSections > 0 ? ProgressStatus.IN_PROGRESS :
                                ProgressStatus.NOT_STARTED
        );

        if(lp.getStatus().equals(ProgressStatus.COMPLETED)) {
                lp.setCompletedAt(LocalDateTime.now());
        }

        lessonProgressRepository.save(lp);

        if(lp.getStatus().equals(ProgressStatus.COMPLETED)) {

                int totalLessons = lessonRepository.countByCourse_CourseId(lesson.getCourse().getCourseId());
                int currentPositionSection = lp.getLesson().getPosition();

                if(currentPositionSection < totalLessons) {
                        Lesson nextLesson = lessonRepository.findByCourse_CourseIdAndPosition(lesson.getCourse().getCourseId(), lesson.getPosition() + 1);

                        LessonProgress nextLp = lessonProgressRepository.findByUser_UserIdAndLesson_LessonId(userId, nextLesson.getLessonId())
                                .orElseThrow(() -> new DataNotFoundException("Lesson progress not found", HttpStatus.NOT_FOUND));

                    nextLp.setStatus(ProgressStatus.IN_PROGRESS);
                    lessonProgressRepository.save(nextLp);
                }
                
                recalculateCourseProgress(userId, lesson.getCourse());
        }
    }

    private void recalculateCourseProgress(UUID userId, Course course) {
        CourseProgress cp = courseProgressRepository.findByUser_UserIdAndCourse_CourseId(userId, course.getCourseId())
                .orElseThrow(() -> new DataNotFoundException("Course progress not found", HttpStatus.NOT_FOUND));

        int totalLessons = cp.getTotalLessons();
        int completedLessons = cp.getCompletedLessons() + 1;

        cp.setTotalLessons(totalLessons);
        cp.setCompletedLessons(completedLessons);
        cp.setStatus(
                completedLessons == totalLessons ? ProgressStatus.COMPLETED :
                        completedLessons > 0 ? ProgressStatus.IN_PROGRESS :
                                ProgressStatus.NOT_STARTED
        );

        if(cp.getStatus().equals(ProgressStatus.COMPLETED)) {
                cp.setCompletedAt(LocalDateTime.now());
        }

        courseProgressRepository.save(cp);

        if(cp.getStatus().equals(ProgressStatus.COMPLETED)) {
                long totalCourses = courseRepository.count();
                int currentPositionCourse = cp.getCourse().getPosition();

                if(currentPositionCourse < totalCourses) {
                        Course nextCourse = courseRepository.findByPosition(currentPositionCourse + 1);

                        CourseProgress nextCp = courseProgressRepository.findByCourse_CourseId(nextCourse.getCourseId())
                                .orElseThrow(() -> new DataNotFoundException("Course progress not found", HttpStatus.NOT_FOUND));

                    nextCp.setStatus(ProgressStatus.IN_PROGRESS);
                    courseProgressRepository.save(nextCp);

                }

        }
    }

    public void recordSpeakingAttempt(String userId, SpeakingRequest speakingRequest) {
        SpeakingAnswer speakingAnswer = new SpeakingAnswer();
        speakingAnswer.setUserId(userId);
        speakingAnswer.setSectionId(speakingRequest.getSectionId());
        speakingAnswer.setSentence(speakingRequest.getSentence());
        speakingAnswer.setAverageScore(speakingRequest.getAverageScore());
        speakingAnswer.setAnsweredAt(Instant.now());
        
        List<WordAnswer> wordAnswers = speakingRequest.getWords().stream()
                .map(wordReq -> new WordAnswer(wordReq.getWord(), wordReq.getScore()))
                .toList();

        speakingAnswer.setWordAnswers(wordAnswers);

        speakingAnswerRepository.save(speakingAnswer);
    }

    private List<SpeakingResponse> convertToSpeakingResponses(List<SpeakingAnswer> answers) {
        return answers.stream().map(answer -> {
            SpeakingResponse response = new SpeakingResponse();
            response.setAverageScore(answer.getAverageScore());
            response.setSentence(answer.getSentence());

            List<WordResponse> wordResponses = answer.getWordAnswers().stream().map(wordAnswer -> {
                WordResponse wordResponse = new WordResponse();

                wordResponse.setWord(wordAnswer.getWord());
                wordResponse.setScore(wordAnswer.getScore());

                return wordResponse;
            }).toList();

            response.setWords(wordResponses);
            return response;
        }).toList();
    }

    public List<SpeakingResponse> completeSpeakingAttempt(String userId, SpeakingRequest speakingRequest) {
        recordSpeakingAttempt(userId, speakingRequest);

        List<SpeakingAnswer> answers = speakingAnswerRepository.findByUserIdAndSectionIdOrderByAnsweredAt(
                userId,
                speakingRequest.getSectionId()
        );

        markSectionCompleted(UUID.fromString(userId), UUID.fromString(speakingRequest.getSectionId()));
        return convertToSpeakingResponses(answers);
    }

    public List<SpeakingResponse> checkLatestSpeakingAttempt(String userId, String sectionId) {
        List<SpeakingAnswer> answers = speakingAnswerRepository.findByUserIdAndSectionIdOrderByAnsweredAt(
                userId,
                sectionId
        );
        
        if(answers == null || answers.isEmpty()){
                return null;
        }

        return convertToSpeakingResponses(answers);
    }
    private AttemptResponse convertToAttemptResponse(MCQAnswer mcqAnswer) {
        int totalQuestions = mcqAnswer.getAnsweredQuestions().size();
        int correctAnswers = (int) mcqAnswer.getAnsweredQuestions().stream().filter(AnsweredQuestion::getIsCorrect).count();
        int score = (int) ((correctAnswers / (double) totalQuestions) * 100);

        AttemptResponse attemptResponse = new AttemptResponse();
        attemptResponse.setSectionId(mcqAnswer.getSectionId());
        attemptResponse.setTotalQuestions(totalQuestions);
        attemptResponse.setCorrectAnswers(correctAnswers);
        attemptResponse.setScore(score);

        List<AnswerResponse> answerResponses = mcqAnswer.getAnsweredQuestions()
                .stream()
                .map(aq -> {
                    AnswerResponse ar = new AnswerResponse();
                    ar.setQuestionId(aq.getQuestionId());
                    ar.setQuestionText(mcqQuestionRepository.findByQuestionId(UUID.fromString(aq.getQuestionId())).getQuestionText());
                    ar.setSelectedOptionId(aq.getSelectedOptionId());
                    ar.setIsCorrect(aq.getIsCorrect());
                    ar.setSelectedOptionText(mCQOptionRepository.findByOptionId(UUID.fromString(aq.getSelectedOptionId())).getOptionText());
                    return ar;
                })
                .toList();
        
        attemptResponse.setAnswers(answerResponses);

        return attemptResponse;
    }

    public AttemptResponse submitAttempt(String userId, SubmitAttemptRequest submitAttemptRequest) {
        MCQAnswer mcqAnswer = new MCQAnswer();
        mcqAnswer.setSectionId(submitAttemptRequest.getSectionId());
        mcqAnswer.setUserId(userId);
        mcqAnswer.setAnsweredAt(Instant.now());
        
        List<AnsweredQuestion> answeredQuestions =
        submitAttemptRequest.getAnswers()
        .stream()
        .map(answerRequest -> {
            AnsweredQuestion aq = new AnsweredQuestion();
            aq.setQuestionId(answerRequest.getQuestionId());
            aq.setSelectedOptionId(answerRequest.getSelectedOptionId());
            aq.setIsCorrect(mCQOptionRepository.findByOptionId(UUID.fromString(answerRequest.getSelectedOptionId())).getIsCorrect());
                
            return aq;
        })
        .toList();

        mcqAnswer.setAnsweredQuestions(answeredQuestions);
        mcqAnswerRepository.save(mcqAnswer);

        markSectionCompleted(UUID.fromString(userId), UUID.fromString(submitAttemptRequest.getSectionId()));

        return convertToAttemptResponse(mcqAnswer);
    }

    public AttemptResponse cekLatestAttempt(String userId, String sectionId) {
        MCQAnswer mcqAnswer = mcqAnswerRepository.findFirstByUserIdAndSectionIdOrderByAnsweredAtDesc(userId, sectionId);
        
        if(mcqAnswer == null) {
            return null;
        }

        return convertToAttemptResponse(mcqAnswer);
    }

}
