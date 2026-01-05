package com.lingulu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class AIConversationResponse {

    private String userText;
    private String aiText;
    private String aiAudioUrl;
}