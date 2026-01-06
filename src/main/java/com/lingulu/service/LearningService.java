package com.lingulu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.dto.CompleteCourseResponse;
import com.lingulu.dto.ProgressResponse;
import com.lingulu.entity.LearningProgress;
import com.lingulu.repository.LearningProgressRepository;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
@Setter
public class LearningService {

    private final LeaderboardService leaderboardService;
    
    private final LearningProgressRepository learningProgressRepository;

    public ProgressResponse getLearningProgress(UUID userId) {
        Integer completedLessons = learningProgressRepository.countByUser_UserIdAndStatus(userId, "COMPLETED");
        Integer totalLessons = 12;
        Float progressPercentage = (completedLessons.floatValue() / totalLessons) * 100;

        ProgressResponse response = ProgressResponse.builder()
                                        .completedLessons(completedLessons)
                                        .totalLessons(totalLessons)
                                        .progressPercentage(progressPercentage)
                                        .build();

        return response;
    }

    public void maskAsComplete(UUID userId, UUID lessonId) {
        LearningProgress progress = learningProgressRepository.findByUser_UserIdAndLesson_LessonId(userId, lessonId);

        if (progress != null) {
            progress.setStatus("COMPLETED");
            learningProgressRepository.save(progress);

            leaderboardService.updateTotalPoints(userId);
        } else {
            throw new RuntimeException("Learning progress not found for the given user and lesson.");
        }
    }

    public List<CompleteCourseResponse> getCompletedCourses(UUID userId) {

        List<LearningProgress> learningProgresses = learningProgressRepository.findByUser_UserId(userId);

        Map<String, String> beginnerLessons = new HashMap<>();
        Map<String, String> intermediateLessons = new HashMap<>();
        Map<String, String> advancedLessons = new HashMap<>();

        int totalLessons = 4;

        int completeBeginnerLessons = 0;
        int completeIntermediateLessons = 0;
        int completeAdvancedLessons = 0;

        for(LearningProgress learningProgress : learningProgresses){
            if(learningProgress.getLesson().getDificultyLevel().equals("Beginner")) {
                beginnerLessons.put(learningProgress.getLesson().getLessonId().toString(), learningProgress.getStatus());

                if(learningProgress.getStatus().equals("Completed")) {
                    completeBeginnerLessons += 1;
                }
            }
            else if(learningProgress.getLesson().getDificultyLevel().equals("Intermediate")){
                intermediateLessons.put(learningProgress.getLesson().getLessonId().toString(), learningProgress.getStatus());

                if(learningProgress.getStatus().equals("Completed")) {
                    completeIntermediateLessons += 1;
                }
            }
            else if(learningProgress.getLesson().getDificultyLevel().equals("Advanced")){
                advancedLessons.put(learningProgress.getLesson().getLessonId().toString(), learningProgress.getStatus());

                if(learningProgress.getStatus().equals("Completed")) {
                    completeAdvancedLessons += 1;
                }
            }
        }

        String beginnerStatus = (completeBeginnerLessons == totalLessons) ? "Completed" : "In Progress";
        String intermediateStatus = (completeIntermediateLessons == totalLessons) ? "Completed" : "In Progress";
        String advancedStatus = (completeAdvancedLessons == totalLessons) ? "Completed" : "In Progress";

        CompleteCourseResponse beginnerLevelResponse = CompleteCourseResponse.builder()
                                                    .listCourses(beginnerLessons)
                                                    .status(beginnerStatus)
                                                    .build();

        CompleteCourseResponse intermediateLevelResponse = CompleteCourseResponse.builder()
                                                    .listCourses(intermediateLessons)
                                                    .status(intermediateStatus)
                                                    .build();

        CompleteCourseResponse advancedLevelResponse = CompleteCourseResponse.builder()
                                                    .listCourses(advancedLessons)
                                                    .status(advancedStatus)
                                                    .build();

        List<CompleteCourseResponse> responses = new ArrayList<>();
        responses.add(beginnerLevelResponse);
        responses.add(intermediateLevelResponse);
        responses.add(advancedLevelResponse);

        return responses;


    }

}
