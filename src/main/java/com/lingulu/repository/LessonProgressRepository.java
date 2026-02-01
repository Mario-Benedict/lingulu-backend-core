package com.lingulu.repository;

import java.util.Optional;
import java.util.UUID;

import com.lingulu.entity.LessonProgress;
import com.lingulu.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.Lesson;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {

    Optional<LessonProgress> findByUser_UserIdAndLesson_LessonId(
            UUID userId,
            UUID lessonId
    );

    int countByUser_UserIdAndLesson_Section_SectionIdAndStatus(
            UUID userId,
            UUID sectionId,
            ProgressStatus status
    );
}
