package com.lingulu.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lingulu.dto.response.course.LessonsResponse;
import com.lingulu.entity.course.LessonProgress;
import com.lingulu.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {

    Optional<LessonProgress> findByUser_UserIdAndLesson_LessonId(
            UUID userId,
            UUID lessonId
    );

    int countByUser_UserIdAndLesson_Course_CourseIdAndStatus(
            UUID userId,
            UUID courseId,
            ProgressStatus status
    );

    @Query("""
        SELECT new com.lingulu.dto.response.course.LessonsResponse(
            l.lessonId,
            lp.status,
            lp.completedAt
        )
        FROM LessonProgress lp
        JOIN lp.lesson l
        WHERE l.course.courseId = :courseId
        AND lp.user.userId = :userId
    """)
    List<LessonsResponse> getProgress(UUID userId, UUID courseId);
}

