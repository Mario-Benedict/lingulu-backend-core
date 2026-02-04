package com.lingulu.controller;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.CompleteLessonsRequest;
import com.lingulu.service.LearningService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.lingulu.security.SecurityUtils.getCurrentUserId;

@RestController
@RequestMapping("/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService learningService;

    @PostMapping("/lessons/complete")
    public ResponseEntity<ApiResponse<?>> completeLesson(@RequestBody @Valid CompleteLessonsRequest completeLessonsRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        UUID lessonId = UUID.fromString(completeLessonsRequest.getLessonId());
        learningService.markLessonCompleted(UUID.fromString(userId),lessonId);
        
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "Lesson mark as completed", null));
    }




}
