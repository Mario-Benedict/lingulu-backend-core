package com.lingulu.repository;

import com.lingulu.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    int countBySection_SectionId(UUID sectionId);

}

