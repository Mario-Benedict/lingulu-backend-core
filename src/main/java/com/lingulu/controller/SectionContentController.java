package com.lingulu.controller;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.AttemptResponse;
import com.lingulu.dto.SectionContentResponse;
import com.lingulu.dto.SpeakingResponse;
import com.lingulu.entity.MCQAnswer;
import com.lingulu.service.LearningService;
import com.lingulu.service.SectionContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionContentController {

    private final SectionContentService sectionContentService;
    private final LearningService learningService;

    @GetMapping("/{sectionId}/content")
    public ResponseEntity<ApiResponse<?>> getSectionContent(
            @PathVariable UUID sectionId
    ) {
        String userId = (String) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();

        List<SpeakingResponse> speakingResponse = learningService.cekLatestSpeakingAttempt(userId, sectionId.toString());

        if(speakingResponse == null || speakingResponse.isEmpty()){
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Section already attempted", speakingResponse)
            );
        }
        

        AttemptResponse attemptResponse = learningService.cekLatestAttempt(userId, sectionId.toString());

        if(attemptResponse != null) {
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Section already attempted", attemptResponse)
            );
        }

        SectionContentResponse response =
                sectionContentService.getSectionContent(sectionId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Section content fetched", response)
        );
    }
}
