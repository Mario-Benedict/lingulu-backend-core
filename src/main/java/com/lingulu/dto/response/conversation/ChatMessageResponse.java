package com.lingulu.dto.response.conversation;

import com.lingulu.enums.ConversationRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    @Enumerated(EnumType.STRING)
    private ConversationRole role;
    private String text;
    private String audioUrl;
    private Instant createdAt;
}

