package com.lingulu.controller;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.CompleteLessonsRequest;
import com.lingulu.dto.CourseResponse;
import com.lingulu.dto.LessonsRequest;
import com.lingulu.dto.LessonsResponse;
import com.lingulu.dto.SectionResponse;
import com.lingulu.dto.SectionsRequest;
import com.lingulu.service.LearningProgressService;
import com.lingulu.service.LearningService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/learning/progress")
@RequiredArgsConstructor
public class LearningProgressController {

    private final LearningProgressService learningProgressService;

    @GetMapping("/lessons")
    public ResponseEntity<ApiResponse<?>> getLessons(@Valid LessonsRequest lessonsRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        UUID sectionId = UUID.fromString(lessonsRequest.getSectionId());

        List<LessonsResponse> lessonsResponses = learningProgressService.getLessons(UUID.fromString(userId), sectionId);

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Lessons progress recieved successfully", lessonsResponses));
    }
    
    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<?>> getSections(@Valid SectionsRequest sectionsRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        UUID courseId = UUID.fromString(sectionsRequest.getCourseId());

        List<SectionResponse> sectionResponses = learningProgressService.getSections(UUID.fromString(userId), courseId);

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Sections progress recieved successfully", sectionResponses));
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
