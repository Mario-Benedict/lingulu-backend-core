package com.lingulu.service;

import com.lingulu.entity.*;
import com.lingulu.entity.Lesson;
import com.lingulu.enums.ProgressStatus;
import com.lingulu.exception.UserNotFoundException;
import com.lingulu.repository.*;
import com.lingulu.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final SectionRepository sectionRepository;
    private final SectionProgressRepository sectionProgressRepository;
    private final LessonRepository lessonsRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseRepository courseRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final UserRepository userRepository;

    public void enrollUserToAllLessons(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.UNAUTHORIZED));

        List<Section> sections = sectionRepository.findAll();

        for (Section section : sections) {
            SectionProgress progress = SectionProgress.builder()
                    .user(user)
                    .section(section)
                    .status(ProgressStatus.valueOf("IN_PROGRESS"))
                    .build();

            sectionProgressRepository.save(progress);
        }

        List<Lesson> lessons = lessonsRepository.findAll();

        for (Lesson lesson : lessons) {
            int position = lesson.getPosition();
            LessonProgress progress = LessonProgress.builder()
                    .user(user)
                    .lesson(lesson)
                    .status(position == 1 ? ProgressStatus.IN_PROGRESS : ProgressStatus.NOT_STARTED)
                    .totalSections(sectionRepository.countByLesson_LessonId(lesson.getLessonId()))
                    .completedSections(0)
                    .build();
            
            lessonProgressRepository.save(progress);
        }

        List<Course> courses = courseRepository.findAll();

        for (Course course : courses) {
            int position = course.getPosition();
            CourseProgress progress = CourseProgress.builder()
                    .user(user)
                    .course(course)
                    .status(position == 1 ? ProgressStatus.IN_PROGRESS : ProgressStatus.NOT_STARTED)
                    .totalLessons(lessonsRepository.countByCourse_CourseId(course.getCourseId()))
                    .completedLessons(0)
                    .build();

            courseProgressRepository.save(progress);
        }
    }
}
