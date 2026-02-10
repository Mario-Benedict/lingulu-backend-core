package com.lingulu.repository.sections;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.sectionType.MCQQuestion;

public interface MCQQuestionRepository extends JpaRepository<MCQQuestion, UUID> {
    MCQQuestion findByQuestionId(UUID questionId);
    
}