package com.lingulu.dto.response.conversation;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIConversationResponse {

    private String userText;
    private String aiText;
    private String userAudioUrl;
    private String aiAudioUrl;
}