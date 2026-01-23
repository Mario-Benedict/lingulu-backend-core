package com.lingulu.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.CompleteCourseResponse;
import com.lingulu.dto.CourseResponse;
import com.lingulu.dto.ProgressResponse;
import com.lingulu.service.LearningService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService learningService;

    private String getUserId() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        return userId;
    }
    
    // @GetMapping("/progress")
    // public ResponseEntity<ApiResponse<?>> progress() {
    //     UUID userId = UUID.fromString(getUserId());

    //     ProgressResponse progress = learningService.getLearningProgress(userId);

    //     return ResponseEntity.ok(new ApiResponse<>(true, "Progress accepted successfully", progress));
    // }

    @GetMapping("/complete")
    public ResponseEntity<ApiResponse<?>> complete() {
        UUID userId = UUID.fromString(getUserId());
        
        Map<String, CourseResponse> progresses = learningService.getCompletedCourses(userId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lessons accepted successfully", progresses));
    }

    @PostMapping("/mark")
    public ResponseEntity<ApiResponse<?>> mark(@RequestBody String courseId){
        UUID userId = UUID.fromString(getUserId());

        learningService.markAsComplete(userId, UUID.fromString(courseId));
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Course marked as complete", null));
    }
}
