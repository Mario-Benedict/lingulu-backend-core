package com.lingulu.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "speaking_answers")
public class SpeakingAnswer {
    @Id
    private String id;
    private String userId;
    private String sectionId;
    private String sentenceId;
    private List<WordAnswer> wordAnswers;
    private float averageScore;
    private Instant answeredAt;
}
