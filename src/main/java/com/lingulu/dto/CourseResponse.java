package com.lingulu.dto;

import com.lingulu.entity.CourseProgress;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CourseResponse {

    private UUID courseId;
    private String courseTitle;

    private String status;

    private int completedSections;
    private int totalSections;
    private float progressPercentage;

    public static CourseResponse from(CourseProgress cp) {
        int percentage =
                cp.getTotalSections() == 0
                        ? 0
                        : (cp.getCompletedSections() * 100) / cp.getTotalSections();

        return CourseResponse.builder()
                .courseId(cp.getCourse().getCourseId())
                .courseTitle(cp.getCourse().getCourseTitle())
                .status(String.valueOf(cp.getStatus()))
                .completedSections(cp.getCompletedSections())
                .totalSections(cp.getTotalSections())
                .progressPercentage(percentage)
                .build();
    }
}
