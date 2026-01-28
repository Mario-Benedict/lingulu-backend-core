package com.lingulu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lingulu.entity.Course;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course,UUID> {

}
