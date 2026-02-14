package com.lingulu.repository;

import com.lingulu.entity.course.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    int countByCourse_CourseId(UUID courseId);
    Lesson findByCourse_CourseIdAndPosition(UUID courseId, int position);
}
