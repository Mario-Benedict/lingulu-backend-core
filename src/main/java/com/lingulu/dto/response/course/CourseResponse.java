package com.lingulu.dto.response.course;

import com.lingulu.entity.course.CourseProgress;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private UUID courseId;
    private String courseTitle;
    private String courseDescription;
    private String difficulty;
    private String status;

    private int completedLessons;
    private int totalLessons;
    private float progressPercentage;

    public static CourseResponse from(CourseProgress cp) {
        int percentage =
                cp.getTotalLessons() == 0
                        ? 0
                        : (cp.getCompletedLessons() * 100) / cp.getTotalLessons();

        return CourseResponse.builder()
                .courseId(cp.getCourse().getCourseId())
                .courseTitle(cp.getCourse().getCourseTitle())
                .difficulty(cp.getCourse().getDifficultyLevel())
                .courseDescription(cp.getCourse().getDescription())
                .status(String.valueOf(cp.getStatus()))
                .completedLessons(cp.getCompletedLessons())
                .totalLessons(cp.getTotalLessons())
                .progressPercentage(percentage)
                .build();
    }
}
