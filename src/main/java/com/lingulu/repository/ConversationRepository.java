package com.lingulu.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lingulu.entity.conversation.Conversation;

public interface ConversationRepository
        extends MongoRepository<Conversation, String> {

    Optional<Conversation> findByUserId(String userId);

}
