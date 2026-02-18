package com.lingulu.dto.response.conversation;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIConversationResponse {
    private String conversationId;

    private String userText;
    private String aiText;

    private String userAudioUrl;
    private String aiAudioUrl;
    private Instant createdAt;
}