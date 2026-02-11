package com.lingulu.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "mcq_answers")
public class MCQAnswer {
    
    @Id
    private String id;
    private String sectionId;
    private String userId;
    private List<AnsweredQuestion> answeredQuestions;
    private Instant answeredAt;

}
