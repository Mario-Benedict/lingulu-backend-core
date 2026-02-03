package com.lingulu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.dto.CourseResponse;
import com.lingulu.dto.LessonsResponse;
import com.lingulu.dto.SectionResponse;
import com.lingulu.entity.CourseProgress;
import com.lingulu.repository.CourseProgressRepository;
import com.lingulu.repository.LessonProgressRepository;
import com.lingulu.repository.SectionProgressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LearningProgressService {
    
    private final LessonProgressRepository lessonProgressRepository;
    private final SectionProgressRepository sectionProgressRepository;
    private final CourseProgressRepository courseProgressRepository;

    public List<LessonsResponse> getLessons(UUID userId, UUID sectionId) {
        List<LessonsResponse> lessonsResponses = lessonProgressRepository.getProgress(userId, sectionId);

        if(lessonsResponses == null || lessonsResponses.isEmpty()){
            throw new IllegalStateException("Unexpected null list");
        }

        return lessonsResponses;
    }

    public List<SectionResponse> getSections(UUID userId, UUID courseId) {
        List<SectionResponse> sectionResponses = sectionProgressRepository.getProgress(userId, courseId);

        if(sectionResponses == null || sectionResponses.isEmpty()){
            throw new IllegalStateException("Unexpected null list");
        }

        return sectionResponses;
    }

    public List<CourseResponse> getCourses(UUID userId) {
        List<CourseProgress> courseProgresses = courseProgressRepository.findByUser_UserId(userId);

        if(courseProgresses == null || courseProgresses.isEmpty()) {
            throw new IllegalStateException("Unexpected null list");
        }

        return courseProgresses
                .stream()
                .map(CourseResponse::from)
                .toList();
    }
}
