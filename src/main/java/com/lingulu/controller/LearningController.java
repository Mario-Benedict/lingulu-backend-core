package com.lingulu.controller;

import com.lingulu.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.lingulu.security.SecurityUtils.getCurrentUserId;

@RestController
@RequestMapping("/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService learningService;

    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<Void> completeLesson(@PathVariable UUID lessonId) {
        UUID userId = getCurrentUserId();
        learningService.markLessonCompleted(userId, lessonId);
        return ResponseEntity.ok().build();
    }
}
