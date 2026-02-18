package com.lingulu.entity.conversation;

import java.time.Instant;

import com.lingulu.enums.ConversationRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessage {

    @Enumerated(EnumType.STRING)
    private ConversationRole role;
    private String audioKey;
    private String transcript;
    private Instant createdAt;
}
