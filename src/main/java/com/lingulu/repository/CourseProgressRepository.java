package com.lingulu.repository;

import com.lingulu.entity.course.CourseProgress;
import com.lingulu.enums.ProgressStatus;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, UUID> {

    Optional<CourseProgress> findByUser_UserIdAndCourse_CourseId(UUID userId, UUID courseId);
    Optional<CourseProgress> findByCourse_CourseId(UUID courseId);

    List<CourseProgress> findByUser_UserId(UUID userId);

    @Query("""
        SELECT cp
        FROM CourseProgress cp
        JOIN cp.user u
        WHERE u.userId = :userId
        AND cp.status = :progressStatus
    """)
    CourseProgress findActiveCourse(@Param("userId") UUID userId, @Param("progressStatus") ProgressStatus progressStatus);
}
