package com.lingulu.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lingulu.dto.SectionResponse;
import com.lingulu.entity.SectionProgress;
import com.lingulu.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SectionProgressRepository extends JpaRepository<SectionProgress, UUID> {

    Optional<SectionProgress> findByUser_UserIdAndSection_SectionId(
            UUID userId,
            UUID sectionID
    );

    int countByUser_UserIdAndSection_Lesson_LessonIdAndStatus(
            UUID userId,
            UUID lessonId,
            ProgressStatus status
    );

    @Query("""
        SELECT new com.lingulu.dto.SectionResponse(
            s.sectionId,
            sp.status,
            sp.completedAt
        )
        FROM SectionProgress sp
        JOIN sp.section s
        WHERE s.lesson.lessonId = :lessonId
        AND sp.user.userId = :userId
    """)
    List<SectionResponse> getProgress(UUID userId, UUID lessonId);
}
