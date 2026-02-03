package com.lingulu.repository;

import com.lingulu.entity.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, UUID> {

    Optional<CourseProgress> findByUser_UserIdAndCourse_CourseId(UUID userId, UUID courseId);
    Optional<CourseProgress> findByCourse_CourseId(UUID courseId);

    List<CourseProgress> findByUser_UserId(UUID userId);
}
