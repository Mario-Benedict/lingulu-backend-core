package com.lingulu.service;

import com.lingulu.dto.CourseResponse;
import com.lingulu.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lingulu.dto.CourseResponse.*;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(course -> builder()
                        .courseId(course.getCourseId())
                        .courseTitle(course.getCourseTitle())
                        .description(course.getDescription())
                        .difficultyLevel(course.getDifficultyLevel())
                        .languageFrom(course.getLanguageFrom())
                        .languageTo(course.getLanguageTo())
                        .published(course.isPublished())
                        .build()
                )
                .toList();
    }
}