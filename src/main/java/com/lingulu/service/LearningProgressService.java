package com.lingulu.service;

import java.util.List;
import java.util.UUID;

import com.lingulu.repository.LessonProgressRepository;
import org.springframework.stereotype.Service;

import com.lingulu.dto.response.course.CourseResponse;
import com.lingulu.dto.response.course.LessonsResponse;
import com.lingulu.dto.response.course.SectionResponse;
import com.lingulu.entity.course.CourseProgress;
import com.lingulu.repository.CourseProgressRepository;
import com.lingulu.repository.SectionProgressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LearningProgressService {
    
    private final SectionProgressRepository sectionProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseProgressRepository courseProgressRepository;

    public List<SectionResponse> getSections(UUID userId, UUID lessonId) {
        List<SectionResponse> sectionsResponses = sectionProgressRepository.getProgress(userId, lessonId);

        if(sectionsResponses == null || sectionsResponses.isEmpty()){
            throw new IllegalStateException("Unexpected null list");
        }

        return sectionsResponses;
    }

    public List<LessonsResponse> getLessons(UUID userId, UUID courseId) {
        List<LessonsResponse> lessonsResponses = lessonProgressRepository.getProgress(userId, courseId);

        if(lessonsResponses == null || lessonsResponses.isEmpty()){
            throw new IllegalStateException("Unexpected null list");
        }

        return lessonsResponses;
    }

    public List<CourseResponse> getCourses(UUID userId) {
        List<CourseProgress> courseProgresses = courseProgressRepository.findByUser_UserIdOrderByCreatedAtAsc(userId);

        if(courseProgresses == null || courseProgresses.isEmpty()) {
            throw new IllegalStateException("Unexpected null list");
        }

        return courseProgresses
                .stream()
                .map(CourseResponse::from)
                .toList();
    }

    public CourseResponse getCourseDetail(UUID userId, UUID courseId) {
        CourseProgress courseProgress = courseProgressRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId)
                .orElseThrow(() -> new IllegalStateException("Course progress not found"));

        return CourseResponse.from(courseProgress);
    }

    public LessonsResponse getLessonDetail(UUID userId, UUID lessonId) {
        return lessonProgressRepository.getLessonProgressByLessonId(userId, lessonId);
    }
}
