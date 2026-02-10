package com.lingulu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lingulu.entity.SpeakingAnswer;

public interface SpeakingAnswerRepository extends MongoRepository<SpeakingAnswer, String>{
    List<SpeakingAnswer> findByUserIdAndSectionId(String userId, String sectionId);
}
