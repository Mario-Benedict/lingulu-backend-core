package com.lingulu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.course.SectionProgress;

public interface LearningProgressRepository extends JpaRepository<SectionProgress, UUID> {

    // Take all learning progress by userId
    List<SectionProgress> findByUser_UserId(UUID userId);

    // Take completed course count
    Integer countByUser_UserIdAndStatus(UUID userId, String status);

    // take lessonProgresses to mark as completed
    SectionProgress findByUser_UserIdAndSection_SectionId(UUID userId, UUID sectionId);

}