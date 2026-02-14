package com.lingulu.entity.course;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Document(collection = "mcq_answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCQAnswer {
    
    @Id
    private String id;
    private String sectionId;
    private String userId;
    private List<AnsweredQuestion> answeredQuestions;
    private Instant answeredAt;

}
