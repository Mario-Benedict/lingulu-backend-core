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

    private final LessonProgressRepository lessonProgressRepository;
    private final SectionProgressRepository sectionProgressRepository;
    private final CourseProgressRepository courseProgressRepository;

    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final LeaderboardService leaderboardService;

    private final CourseRepository courseRepository;

    public void markLessonCompleted(UUID userId, UUID lessonId) {

        LessonProgress lp = lessonProgressRepository.findByUser_UserIdAndLesson_LessonId(userId, lessonId)
                                .orElseThrow(() -> new DataNotFoundException("Lesson progress not found", HttpStatus.BAD_REQUEST));

        if (lp.getStatus() == ProgressStatus.COMPLETED) return;

        lp.setStatus(ProgressStatus.COMPLETED);
        lp.setCompletedAt(LocalDateTime.now());
        lessonProgressRepository.save(lp);

        recalcSectionProgress(userId, lp.getLesson().getSection());
        leaderboardService.updateTotalPoints(userId);
    }

    private void recalcSectionProgress(UUID userId, Section section) {

        // int totalLessons =
        //         lessonRepository.countBySection_SectionId(section.getSectionId());

        // int completedLessons =
        //         lessonProgressRepository.countByUser_UserIdAndLesson_Section_SectionIdAndStatus(
        //                 userId,
        //                 section.getSectionId(),
        //                 ProgressStatus.COMPLETED
        //         );

        SectionProgress sp = sectionProgressRepository
                .findByUser_UserIdAndSection_SectionId(userId, section.getSectionId())
                .orElseThrow(() -> new DataNotFoundException("Section progress not found", HttpStatus.NOT_FOUND));

        int totalLessons = sp.getTotalLessons();
        int completedLessons = sp.getCompletedLessons() + 1;

        // sp.setTotalLessons(totalLessons);
        sp.setCompletedLessons(completedLessons);
        sp.setStatus(
                completedLessons == totalLessons ? ProgressStatus.COMPLETED :
                        completedLessons > 0 ? ProgressStatus.IN_PROGRESS :
                                ProgressStatus.NOT_STARTED
        );

        if(sp.getStatus().equals(ProgressStatus.COMPLETED)) {
                sp.setCompletedAt(LocalDateTime.now());
        }

        sectionProgressRepository.save(sp);

        if(sp.getStatus().equals(ProgressStatus.COMPLETED)) {

                int jumlahSection = sectionRepository.countByCourse_CourseId(section.getCourse().getCourseId());
                int currentPositionSection = sp.getSection().getPosition();

                if(currentPositionSection < jumlahSection) {
                        Section nextSection = sectionRepository.findByCourse_CourseIdAndPosition(section.getCourse().getCourseId(), section.getPosition() + 1);

                        SectionProgress nextSp = sectionProgressRepository.findByUser_UserIdAndSection_SectionId(userId, nextSection.getSectionId())
                                .orElseThrow(() -> new DataNotFoundException("Section progress not found", HttpStatus.NOT_FOUND));
                
                        if(nextSp != null) {
                                nextSp.setStatus(ProgressStatus.IN_PROGRESS);
                                sectionProgressRepository.save(nextSp);
                        }
                }
                
                recalcCourseProgress(userId, section.getCourse());
        }
    }

    private void recalcCourseProgress(UUID userId, Course course) {

        // int totalSections =
        //         sectionRepository.countByCourse_CourseId(course.getCourseId());

        // int completedSections =
        //         sectionProgressRepository.countByUser_UserIdAndSection_Course_CourseIdAndStatus(
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

        int totalSections = cp.getTotalSections();
        int completedSections = cp.getCompletedSections() + 1;

        cp.setTotalSections(totalSections);
        cp.setCompletedSections(completedSections);
        cp.setStatus(
                completedSections == totalSections ? ProgressStatus.COMPLETED :
                        completedSections > 0 ? ProgressStatus.IN_PROGRESS :
                                ProgressStatus.NOT_STARTED
        );

        if(cp.getStatus().equals(ProgressStatus.COMPLETED)) {
                cp.setCompletedAt(LocalDateTime.now());
        }

        courseProgressRepository.save(cp);

        if(cp.getStatus().equals(ProgressStatus.COMPLETED)) {
                long jumlahCourses = courseRepository.count();
                int currentPositionCourse = cp.getCourse().getPosition();

                if(currentPositionCourse < jumlahCourses) {
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
