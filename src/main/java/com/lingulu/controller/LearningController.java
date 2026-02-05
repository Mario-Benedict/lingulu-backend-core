package com.lingulu.controller;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.CompleteSectionsRequest;
import com.lingulu.service.LearningService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService learningService;

    @PostMapping("/lesson/complete")
    public ResponseEntity<ApiResponse<?>> completeLesson(@RequestBody @Valid CompleteSectionsRequest completeSectionsRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        UUID sectionId = UUID.fromString(completeSectionsRequest.getSectionId());
        learningService.markSectionCompleted(UUID.fromString(userId),sectionId);
        
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "Section mark as completed", null));
    }




}
