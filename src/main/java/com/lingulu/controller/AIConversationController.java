package com.lingulu.controller;

import com.lingulu.dto.response.conversation.ConversationHistoryResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.lingulu.dto.response.conversation.AIConversationResponse;
import com.lingulu.dto.response.general.ApiResponse;
import com.lingulu.dto.request.conversation.ConversationRequest;
import com.lingulu.service.ConversationService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/conversation")
public class AIConversationController {

    private final ConversationService conversationService;

    public AIConversationController(
            ConversationService conversationService
    ) {
        this.conversationService = conversationService;
    }

    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<AIConversationResponse>> conversation(
            @ModelAttribute @Valid ConversationRequest request
    ) throws Exception {

        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        AIConversationResponse response =
                conversationService.process(
                        request.getAudio(),
                        userId
                );

        return ResponseEntity.ok()
                .body(new ApiResponse<> (true, "Chat reply successfully", response)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<ConversationHistoryResponse>> history() {

        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        ConversationHistoryResponse response =
                conversationService.loadHistory(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Conversation history loaded successfully",
                        response
                )
        );
    }
}
