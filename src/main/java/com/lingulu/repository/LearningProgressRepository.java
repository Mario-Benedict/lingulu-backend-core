package com.lingulu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.LessonProgress;

public interface LearningProgressRepository extends JpaRepository<LessonProgress, UUID> {

    // Take all learning progress by userId
    List<LessonProgress> findByUser_UserId(UUID userId);

    // Take completed course count
    Integer countByUser_UserIdAndStatus(UUID userId, String status);

    // take lessonProgresses to mark as completed
    LessonProgress findByUser_UserIdAndLesson_LessonId(UUID userId, UUID lessonId);

}