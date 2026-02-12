package com.lingulu.controller;

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
@RequestMapping("/api")
public class AIConversationController {

    private final ConversationService conversationService;

    public AIConversationController(
            ConversationService conversationService
    ) {
        this.conversationService = conversationService;
    }

    @PostMapping(
        value = "/conversation",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<AIConversationResponse>> conversation(
            @ModelAttribute @Valid ConversationRequest request
    ) throws Exception {

        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        return ResponseEntity.ok()
                .body(new ApiResponse<> (true, "Chat reply successfully", conversationService.process(request.getAudio(), request.getConversationId(), userId))
        );
    }
}
