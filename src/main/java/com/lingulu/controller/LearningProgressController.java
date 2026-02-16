package com.lingulu.controller;

import com.lingulu.dto.request.course.LessonsRequest;
import com.lingulu.dto.request.course.SectionsRequest;
import com.lingulu.dto.response.general.ApiResponse;
import com.lingulu.dto.response.course.CourseResponse;
import com.lingulu.dto.response.course.LessonsResponse;
import com.lingulu.dto.response.course.SectionResponse;
import com.lingulu.service.LearningProgressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/api/learning/progress")
@RequiredArgsConstructor
public class LearningProgressController {

    private final LearningProgressService learningProgressService;

    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<?>> getSections(@Valid @ModelAttribute SectionsRequest sectionsRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        UUID lessonId = UUID.fromString(sectionsRequest.getLessonId());

        List<SectionResponse> sectionResponses = learningProgressService.getSections(UUID.fromString(userId), lessonId);

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Sections progress received successfully", sectionResponses));
    }
    
    @GetMapping("/lessons")
    public ResponseEntity<ApiResponse<?>> getLessons(@Valid @ModelAttribute LessonsRequest lessonsRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        UUID courseId = UUID.fromString(lessonsRequest.getCourseId());

        List<LessonsResponse> lessonsResponses = learningProgressService.getLessons(UUID.fromString(userId), courseId);

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Lessons progress received successfully", lessonsResponses));
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<?>> getCourses() {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        List<CourseResponse> courseResponses = learningProgressService.getCourses(UUID.fromString(userId));

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Courses progress recieved successfully", courseResponses));
    }
    
}
