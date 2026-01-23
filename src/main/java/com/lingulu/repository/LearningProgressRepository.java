package com.lingulu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.LearningProgress;

public interface LearningProgressRepository extends JpaRepository<LearningProgress, UUID> {

    // Take all learning progress by userId
    List<LearningProgress> findByUser_UserId(UUID userId);

    // Take completed course count
    Integer countByUser_UserIdAndStatus(UUID userId, String status);

    // take learningProgress to mark as completed
    LearningProgress findByUser_UserIdAndLesson_LessonId(UUID userId, UUID lessonId);

}