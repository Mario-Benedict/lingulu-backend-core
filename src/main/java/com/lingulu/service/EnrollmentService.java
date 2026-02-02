package com.lingulu.service;

import com.lingulu.entity.Course;
import com.lingulu.entity.CourseProgress;
import com.lingulu.entity.Lesson;
import com.lingulu.entity.LessonProgress;
import com.lingulu.entity.Section;
import com.lingulu.entity.SectionProgress;
import com.lingulu.entity.User;
import com.lingulu.enums.ProgressStatus;
import com.lingulu.exception.UserNotFoundException;
import com.lingulu.repository.CourseProgressRepository;
import com.lingulu.repository.CourseRepository;
import com.lingulu.repository.LessonProgressRepository;
import com.lingulu.repository.LessonRepository;
import com.lingulu.repository.SectionProgressRepository;
import com.lingulu.repository.SectionRepository;
import com.lingulu.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final SectionRepository sectionRepository;
    private final SectionProgressRepository sectionProgressRepository;
    private final CourseRepository courseRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final UserRepository userRepository;

    public void enrollUserToAllLessons(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.UNAUTHORIZED));

        List<Lesson> lessons = lessonRepository.findAll();

        for (Lesson lesson : lessons) {
            LessonProgress progress = LessonProgress.builder()
                    .user(user)
                    .lesson(lesson)
                    .status(ProgressStatus.valueOf("IN_PROGRESS"))
                    .build();

            lessonProgressRepository.save(progress);
        }

        List<Section> sections = sectionRepository.findAll();

        for (Section section : sections) {
            int position = section.getPosition();
            SectionProgress progress = SectionProgress.builder()
                    .user(user)
                    .section(section)
                    .status(position == 1 ? ProgressStatus.IN_PROGRESS : ProgressStatus.NOT_STARTED)
                    .totalLessons(lessonRepository.countBySection_SectionId(section.getSectionId()))
                    .completedLessons(0)
                    .build();
            
            sectionProgressRepository.save(progress);
        }

        List<Course> courses = courseRepository.findAll();

        for (Course course : courses) {
            int position = course.getPosition();
            CourseProgress progress = CourseProgress.builder()
                    .user(user)
                    .course(course)
                    .status(position == 1 ? ProgressStatus.IN_PROGRESS : ProgressStatus.NOT_STARTED)
                    .totalSections(sectionRepository.countByCourse_CourseId(course.getCourseId()))
                    .completedSections(0)
                    .build();

            courseProgressRepository.save(progress);
        }
    }
}
