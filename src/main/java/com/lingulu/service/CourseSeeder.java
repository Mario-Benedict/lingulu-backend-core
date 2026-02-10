package com.lingulu.service;

import java.io.File;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingulu.dto.CourseSeed;
import com.lingulu.entity.Course;
import com.lingulu.repository.CourseRepository;
import com.lingulu.repository.LessonRepository;
import com.lingulu.repository.SectionRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CourseSeeder implements CommandLineRunner {

    private final S3StorageService s3StorageService;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    



    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        File seedDir = new File("D:\\CAWU 4\\Software Enginnering\\Project\\lingulu-backend-core\\seed");
        File[] files = seedDir.listFiles(f -> f.getName().endsWith(".json"));

        for (File file : files) {
            // CourseSeed seed = mapper.readValue(file, CourseSeed.class);
            // seedCourse(seed);
            // System.out.println(file.getName());
        }
    }

    private void seedCourse(CourseSeed seed) {
        // Course course = Course.builder()
        //         .title(seed.getCourseTitle())
        //         .description(seed.description())
        //         .position(seed.position())
        //         .imagePath(s3StorageService.uploadFile(new File(seed.imagePath())))
        //         .build();
        // courseRepository.save(course);
    }
}
