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
                cp.getTotalLessons() == 0
                        ? 0
                        : (cp.getCompletedLessons() * 100) / cp.getTotalLessons();

        return CourseResponse.builder()
                .courseId(cp.getCourse().getCourseId())
                .courseTitle(cp.getCourse().getCourseTitle())
                .status(String.valueOf(cp.getStatus()))
                .completedLessons(cp.getCompletedLessons())
                .totalLessons(cp.getTotalLessons())
                .progressPercentage(percentage)
                .build();
    }
}
