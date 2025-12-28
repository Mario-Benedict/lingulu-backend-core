package com.lingulu.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lingulu.dto.AIConversationResponse;
import com.lingulu.service.ConversationService;

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
    public ResponseEntity<AIConversationResponse> conversation(
            @RequestPart("audio") MultipartFile audio,
            @RequestParam("conversationId") String conversationId
    ) throws Exception {

        if (audio == null || audio.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        return ResponseEntity.ok(
                conversationService.process(audio, conversationId, userId)
        );
    }
}
