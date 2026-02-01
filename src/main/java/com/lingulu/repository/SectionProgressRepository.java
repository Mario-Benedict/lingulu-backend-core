package com.lingulu.repository;

import java.util.Optional;
import java.util.UUID;

import com.lingulu.entity.SectionProgress;
import com.lingulu.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionProgressRepository extends JpaRepository<SectionProgress, UUID> {

    Optional<SectionProgress> findByUser_UserIdAndSection_SectionId(
            UUID userId,
            UUID sectionId
    );

    int countByUser_UserIdAndSection_Course_CourseIdAndStatus(
            UUID userId,
            UUID courseId,
            ProgressStatus status
    );
}

