package com.lingulu.repository;

import com.lingulu.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {

    int countByCourse_CourseId(UUID courseId);

}
