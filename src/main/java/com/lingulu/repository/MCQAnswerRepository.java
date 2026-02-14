package com.lingulu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lingulu.entity.course.MCQAnswer;

public interface MCQAnswerRepository extends MongoRepository<MCQAnswer, String>{
    MCQAnswer findFirstByUserIdAndSectionIdOrderByAnsweredAtDesc(String userId, String sectionId);
}
