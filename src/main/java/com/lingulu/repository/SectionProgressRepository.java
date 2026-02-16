package com.lingulu.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lingulu.dto.response.course.SectionResponse;
import com.lingulu.entity.course.SectionProgress;
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
        SELECT new com.lingulu.dto.response.course.SectionResponse(
            s.sectionId,
            s.sectionTitle,
            s.sectionType,
            sp.status,
            sp.completedAt
        )
        FROM SectionProgress sp
        JOIN sp.section s
        WHERE s.lesson.lessonId = :lessonId
        AND sp.user.userId = :userId
        ORDER BY s.position ASC
    """)
    List<SectionResponse> getProgress(UUID userId, UUID lessonId);

    @Query("""
        SELECT sp
        FROM SectionProgress sp
        JOIN sp.section s
        WHERE s.sectionId = :sectionId
        AND sp.user.userId = :userId
    """)
    SectionProgress getSectionProgressBysectionId(UUID userId, UUID sectionId);
}
