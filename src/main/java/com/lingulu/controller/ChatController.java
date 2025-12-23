package com.lingulu.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.ChatRequest;
import com.lingulu.dto.ChatResponse;
import com.lingulu.service.GroqService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/conversation")
@CrossOrigin
@AllArgsConstructor
public class ChatController {

    private final GroqService groqService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = groqService.chat(request.getMessage());
        return new ChatResponse(reply);
    }
}

