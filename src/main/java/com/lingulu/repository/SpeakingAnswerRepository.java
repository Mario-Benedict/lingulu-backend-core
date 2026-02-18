package com.lingulu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lingulu.entity.course.SpeakingAnswer;

public interface SpeakingAnswerRepository extends MongoRepository<SpeakingAnswer, String>{
    List<SpeakingAnswer> findByUserIdAndSectionIdOrderByAnsweredAt(String userId, String sectionId);

    Optional<SpeakingAnswer> findByUserIdAndSectionIdAndSpeakingId(String userId, String sectionId, String speakingId);
}
