package com.lingulu.service;

import com.lingulu.entity.*;
import com.lingulu.enums.ProgressStatus;
import com.lingulu.exception.DataNotFoundException;
import com.lingulu.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private final CourseRepository courseRepository;

    public void markSectionCompleted(UUID userId, UUID sectionId) {

        SectionProgress sp = sectionProgressRepository.findByUser_UserIdAndSection_SectionId(userId, sectionId)
                                .orElseThrow(() -> new DataNotFoundException("Section progress not found", HttpStatus.BAD_REQUEST));

        if (sp.getStatus() == ProgressStatus.COMPLETED) return;

        sp.setStatus(ProgressStatus.COMPLETED);
        sp.setCompletedAt(LocalDateTime.now());
        sectionProgressRepository.save(sp);

        recalcLessonProgress(userId, sp.getSection().getLesson());
        leaderboardService.updateTotalPoints(userId);
        userLearningStatsService.updateStreak(userId);
    }

    private void recalcLessonProgress(UUID userId, Lesson lesson) {

        // int totalLessons =
        //         sectionRepository.countByLesson_LessonId(lesson.getSectionId());

        // int completedLessons =
        //         lessonProgressRepository.countByUser_UserIdAndLesson_Section_SectionIdAndStatus(
        //                 userId,
        //                 lesson.getSectionId(),
        //                 ProgressStatus.COMPLETED
        //         );

        LessonProgress lp = lessonProgressRepository
                .findByUser_UserIdAndLesson_LessonId(userId, lesson.getLessonId())
                .orElseThrow(() -> new DataNotFoundException("Lesson progress not found", HttpStatus.NOT_FOUND));

        int totalSections = lp.getTotalSections();
        int completedSections = lp.getCompletedSections() + 1;

        // sp.setTotalLessons(totalLessons);
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
                
                        if(nextLp != null) {
                                nextLp.setStatus(ProgressStatus.IN_PROGRESS);
                                lessonProgressRepository.save(nextLp);
                        }
                }
                
                recalcCourseProgress(userId, lesson.getCourse());
        }
    }

    private void recalcCourseProgress(UUID userId, Course course) {

        // int totalSections =
        //         lessonsRepository.countByCourse_CourseId(course.getCourseId());

        // int completedSections =
        //         lessonProgressRepository.countByUser_UserIdAndLesson_Course_CourseIdAndStatus(
        //                 userId,
        //                 course.getCourseId(),
        //                 ProgressStatus.COMPLETED
        //         );

        // CourseProgress cp = courseProgressRepository
        //         .findByUser_UserId(userId)
        //         .stream()
        //         .filter(p -> p.getCourse().getCourseId().equals(course.getCourseId()))
        //         .findFirst()
        //         .orElseGet(() -> {
        //             CourseProgress c = new CourseProgress();
        //             c.setUser(userRepository.getReferenceById(userId));
        //             c.setCourse(course);
        //             return c;
        //         });

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

                        if(nextCp != null) {
                                nextCp.setStatus(ProgressStatus.IN_PROGRESS);
                                courseProgressRepository.save(nextCp);
                        }

                }

        }
    }

}
