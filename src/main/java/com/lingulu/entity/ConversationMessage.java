package com.lingulu.entity;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessage {

    private String role;       // USER / AI
    private String audioUrl;   // S3 path
    private String transcript;
    private Instant createdAt;
}
