package com.lingulu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lingulu.entity.conversation.Conversation;

public interface ConversationRepository
        extends MongoRepository<Conversation, String> {
}
