package com.lingulu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.LearningProgress;

public interface LearningProgressRepository extends JpaRepository<LearningProgress, UUID> {

    // Take learning progress by user id and difficulty level
    List<LearningProgress> findByUser_UserIdAndLesson_difficultyLevel(UUID userId, String difficultyLevel);

    // Take completed course count
    Integer countByUser_UserIdAndStatus(UUID userId, String status);

    // take learningProgress to mark as completed
    LearningProgress findByUser_UserIdAndLesson_LessonId(UUID userId, UUID lessonId);

    // count by difficulty level (aka level test)
    Integer countByLesson_difficultyLevelAndUser_userIdAndStatus(String difficultyLevel, UUID userId, String status);

}