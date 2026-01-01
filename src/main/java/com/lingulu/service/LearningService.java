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

@Service
@RequiredArgsConstructor
public class LearningService {
    
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
        } else {
            throw new RuntimeException("Learning progress not found for the given user and lesson.");
        }
    }

    public List<CompleteCourseResponse> getCompletedCourses(UUID userId) {

        Integer totalLessonsPerLevel = 4;

        Integer beginerLevelLessons = learningProgressRepository.countByLesson_difficultyLevelAndUser_userIdAndStatus("Beginner", userId, "COMPLETED");

        Map<String, String> beginerLessons = new HashMap<>();

        List<LearningProgress> beginerProgressList = learningProgressRepository.findByUser_UserIdAndLesson_difficultyLevel(userId, "Beginner");
        for (LearningProgress progress : beginerProgressList) {
            beginerLessons.put(progress.getLesson().getLessonId().toString(), progress.getStatus());
        }

        String beginerStatus;
        if(beginerLevelLessons.equals(totalLessonsPerLevel)){ 
            beginerStatus = "Completed";
        }else{
            beginerStatus = "In Progress";
        }

        CompleteCourseResponse beginerLevelResponse = CompleteCourseResponse.builder()
                                                    .listCourses(beginerLessons)
                                                    .status(beginerStatus)
                                                    .build();


        Integer intermediateLevelLessons = learningProgressRepository.countByLesson_difficultyLevelAndUser_userIdAndStatus("Intermediate", userId, "COMPLETED");

        Map<String, String> intermediateLessons = new HashMap<>();

        List<LearningProgress> intermediateProgressList = learningProgressRepository.findByUser_UserIdAndLesson_difficultyLevel(userId, "Intermediate");
        for (LearningProgress progress : intermediateProgressList) {    
            intermediateLessons.put(progress.getLesson().getLessonId().toString(), progress.getStatus());
        }
        
        String intermediateStatus;
        if(intermediateLevelLessons.equals(totalLessonsPerLevel)){ 
            intermediateStatus = "Completed";
        }else{
            intermediateStatus = "In Progress";
        }

        CompleteCourseResponse intermediateLevelResponse = CompleteCourseResponse.builder()
                                                    .listCourses(intermediateLessons)
                                                    .status(intermediateStatus)
                                                    .build();
    
        Integer advancedLevelLessons = learningProgressRepository.countByLesson_difficultyLevelAndUser_userIdAndStatus("Advanced", userId, "COMPLETED");

        Map<String, String> advancedLessons = new HashMap<>();

        List<LearningProgress> advancedProgressList = learningProgressRepository.findByUser_UserIdAndLesson_difficultyLevel(userId, "Advanced");
        for (LearningProgress progress : advancedProgressList) {
            advancedLessons.put(progress.getLesson().getLessonId().toString(), progress.getStatus());
        }
        String advancedStatus;
        if(advancedLevelLessons.equals(totalLessonsPerLevel)){ 
            advancedStatus = "Completed";
        }else{
            advancedStatus = "In Progress";
        }
        CompleteCourseResponse advancedLevelResponse = CompleteCourseResponse.builder()
                                                    .listCourses(advancedLessons)
                                                    .status(advancedStatus)
                                                    .build();
        

        List<CompleteCourseResponse> responses = new ArrayList<>();
        responses.add(beginerLevelResponse);
        responses.add(intermediateLevelResponse);
        responses.add(advancedLevelResponse);

        return responses;
    }

}
