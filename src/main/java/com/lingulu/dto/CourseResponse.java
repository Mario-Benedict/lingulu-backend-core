package com.lingulu.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Builder
public class CourseResponse {
    private UUID courseId;
    private String difficultyLevel;
    private String courseTitle;
    private String description;
    private String languageFrom;
    private String languageTo;
    private boolean published;
}
