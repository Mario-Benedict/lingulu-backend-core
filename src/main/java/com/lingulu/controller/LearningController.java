package com.lingulu.controller;

import com.lingulu.dto.response.general.ApiResponse;
import com.lingulu.dto.response.course.AttemptResponse;
import com.lingulu.dto.request.course.CompleteSectionsRequest;
import com.lingulu.dto.request.course.SpeakingRequest;
import com.lingulu.dto.response.course.SpeakingResponse;
import com.lingulu.dto.request.course.SubmitAttemptRequest;
import com.lingulu.service.LearningService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService learningService;

    @PostMapping("/section/complete")
    public ResponseEntity<ApiResponse<?>> completeLesson(@RequestBody @Valid CompleteSectionsRequest completeSectionsRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        UUID sectionId = UUID.fromString(completeSectionsRequest.getSectionId());
        learningService.markSectionCompleted(UUID.fromString(userId),sectionId);
        
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "Section mark as completed", null));
    }

    @PostMapping("/section/speaking/attempt")
    public ResponseEntity<ApiResponse<?>> postMethodName(@RequestBody @Valid SpeakingRequest speakingRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        learningService.recordSpeakingAttempt(userId, speakingRequest);
        
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "Speaking attempt recorded", speakingRequest));
    }
    
    @PostMapping("/section/speaking/complete")
    public ResponseEntity<ApiResponse<?>> completeSpeakingAttempt(@RequestBody @Valid SpeakingRequest speakingRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
      
        List<SpeakingResponse> speakingResponse = learningService.completeSpeakingAttempt(userId, speakingRequest);
        
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "Speaking attempt completed", speakingResponse));
    }
  
    @PostMapping("/section/attempt")
    public ResponseEntity<ApiResponse<?>> submitAttempt(@RequestBody @Valid SubmitAttemptRequest submitAttemptRequest) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        AttemptResponse attemptResponse = learningService.submitAttempt(userId, submitAttemptRequest);
        
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "Attempt submitted", attemptResponse));
    }
    

        
    

}
