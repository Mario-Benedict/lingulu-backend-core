package com.lingulu.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id; // conversationId

    private String userId;

    private List<ConversationMessage> messages = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}
