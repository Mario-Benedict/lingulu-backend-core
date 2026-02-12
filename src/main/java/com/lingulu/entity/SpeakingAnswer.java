package com.lingulu.entity;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Document(collection = "speaking_answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingAnswer {
    @Id
    private String id;
    private String userId;
    private String sectionId;
    private String sentence;
    private List<WordAnswer> wordAnswers;
    private float averageScore;
    private Instant answeredAt;
}
