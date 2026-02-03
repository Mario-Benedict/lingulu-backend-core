package com.lingulu.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.Course;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    Course findByPosition(int position);
}
