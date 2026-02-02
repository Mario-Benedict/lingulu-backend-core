package com.lingulu.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lingulu.entity.LessonProgress;
import com.lingulu.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lingulu.dto.LessonsResponse;
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

    @Query("""
        SELECT new com.lingulu.dto.LessonsResponse(
            l.lessonId,
            lp.status,
            lp.completedAt
        )
        FROM LessonProgress lp
        JOIN lp.lesson l
        WHERE l.section.sectionId = :sectionId
        AND lp.user.userId = :userId
    """)
    List<LessonsResponse> getProgress(UUID userId, UUID sectionId);
}
