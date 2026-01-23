package com.lingulu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    // private UUID courseId;
    // private String difficultyLevel;
    // private String courseTitle;
    // private String description;
    // private String languageFrom;
    // private String languageTo;
    // private boolean published;
    private Map<String, SectionResponse> sections;
    private String status;
    private int totalSections;
    private int completedSections;
    private float progressPercentage;
}
