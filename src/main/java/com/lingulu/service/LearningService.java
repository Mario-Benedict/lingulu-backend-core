package com.lingulu.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.dto.CompleteCourseResponse;
import com.lingulu.dto.CourseResponse;
import com.lingulu.dto.ProgressResponse;
import com.lingulu.dto.SectionResponse;
import com.lingulu.entity.Course;
import com.lingulu.entity.LearningProgress;
import com.lingulu.entity.Lesson;
import com.lingulu.entity.Section;
import com.lingulu.entity.User;
import com.lingulu.repository.CourseRepository;
import com.lingulu.repository.LearningProgressRepository;
import com.lingulu.repository.LessonRepository;
import com.lingulu.repository.SectionRepository;
import com.lingulu.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
@Setter
public class LearningService {

    private final LeaderboardService leaderboardService;
    
    private final LearningProgressRepository learningProgressRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    

    // public ProgressResponse getLearningProgress(UUID userId) {
    //     // Update jadi return progress semua courses (beginner, inter, advanced)
    //     Integer completedLessons = learningProgressRepository.countByUser_UserIdAndStatus(userId, "COMPLETED");
    //     Integer totalLessons = 12;
    //     Float progressPercentage = (completedLessons.floatValue() / totalLessons) * 100;

    //     ProgressResponse response = ProgressResponse.builder()
    //                                     .completedLessons(completedLessons)
    //                                     .totalLessons(totalLessons)
    //                                     .progressPercentage(progressPercentage)
    //                                     .build();

    //     return response;
    // }

    public void markAsComplete(UUID userId, UUID lessonId) {
        LearningProgress progress = learningProgressRepository.findByUser_UserIdAndLesson_LessonId(userId, lessonId);

        if (progress != null) {
            progress.setStatus("COMPLETED");
            progress.setCompletedAt(LocalDateTime.now());
            learningProgressRepository.save(progress);

            leaderboardService.updateTotalPoints(userId);
        } else {
            throw new RuntimeException("Learning progress not found for the given user and lesson.");
        }
    }

    public Map<String, CourseResponse> getCompletedCourses(UUID userId) {
        List<LearningProgress> learningProgresses = learningProgressRepository.findByUser_UserId(userId);

        Map<String, CourseResponse> courseResponse = new HashMap<>();

        for (LearningProgress learn : learningProgresses) {
            String courseId = learn.getLesson().getSection().getCourse().getCourseId().toString();
            String sectionId = learn.getLesson().getSection().getSectionId().toString();
            String lessonId = learn.getLesson().getLessonId().toString();
            String status = learn.getStatus();

            // Pastikan CourseResponse ada
            courseResponse.putIfAbsent(courseId, CourseResponse.builder()
                    .sections(new HashMap<>())
                    .status("In Progress")
                    .totalSections(0)
                    .completedSections(0)
                    .progressPercentage(0)
                    .build());

            CourseResponse course = courseResponse.get(courseId);

            // Pastikan SectionResponse ada
            course.getSections().putIfAbsent(sectionId, SectionResponse.builder()
                    .lessons(new HashMap<>())
                    .status("In Progress")
                    .completedLessons(0)
                    .totalLessons(0)
                    .build());

            SectionResponse section = course.getSections().get(sectionId);

            // Jika section baru, update totalSections
            if (section.getTotalLessons() == 0 && section.getLessons().isEmpty()) {
                course.setTotalSections(course.getTotalSections() + 1);
            }

            // Update totalLessons
            section.setTotalLessons(section.getTotalLessons() + 1);

            // Update completedLessons jika status Completed
            if ("Completed".equalsIgnoreCase(status)) {
                section.setCompletedLessons(section.getCompletedLessons() + 1);
            }

            // Masukkan lesson ke map
            section.getLessons().put(lessonId, status);
        }

        // Hitung status section dan course, serta progress
        for (CourseResponse course : courseResponse.values()) {
            int completedSectionsCount = 0;

            for (SectionResponse section : course.getSections().values()) {
                if (section.getCompletedLessons() == section.getTotalLessons()) {
                    section.setStatus("Completed");
                    completedSectionsCount++;
                }
            }

            course.setCompletedSections(completedSectionsCount);

            if (course.getCompletedSections() == course.getTotalSections()) {
                course.setStatus("Completed");
            }

            // Hitung progressPercentage, hindari pembagian integer langsung
            if (course.getTotalSections() > 0) {
                course.setProgressPercentage((int) ((double) course.getCompletedSections() / course.getTotalSections() * 100));
            } else {
                course.setProgressPercentage(0);
            }
        }

        return courseResponse;
    }

    public void addUserLessons(UUID userId) {
        List<Lesson> lessons = lessonRepository.findAll();

        for(Lesson lesson : lessons) {

            User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
            LearningProgress learningProgress = LearningProgress.builder().user(user).lesson(lesson).status("In Progress").completedAt(null).build();

            learningProgressRepository.save(learningProgress);
        }
    }

    // OLD CompletedCourseFUnction, Aku belum delete buat jaga jaga
    // public Map<String, CourseResponse> getCompletedCourses(UUID userId) {
    //     // update jadi response terbaru

    //     List<LearningProgress> learningProgresses = learningProgressRepository.findByUser_UserId(userId);

    //     Map<String, CourseResponse> courseResponse = new HashMap<>();

    //     for(LearningProgress learn : learningProgresses){
    //         String courseId = learn.getLesson().getSection().getCourse().getCourseId().toString();
    //         String sectionId = learn.getLesson().getSection().getSectionId().toString();
    //         String lessonId = learn.getLesson().getLessonId().toString();
    //         String status = learn.getStatus();

    //         if(courseResponse.get(courseId) == null){
    //             courseResponse.put(courseId, CourseResponse.builder().sections(new HashMap<>()).status("In Progress").totalSections(0).completedSections(0).progressPercentage(0).build());

    //             if(courseResponse.get(courseId).getSections().get(sectionId) == null){
    //                 courseResponse.get(courseId).getSections().put(sectionId, SectionResponse.builder().lessons(new HashMap<>()).status("In Progress").completedLessons(0).totalLessons(0).build());
                    
    //                 int currentTotalSections =  courseResponse.get(courseId).getTotalSections();
        
    //                 courseResponse.get(courseId).setTotalSections(currentTotalSections + 1);
            
    //             }
            
    //         }
    //         int currentTotalLessons = courseResponse.get(courseId).getSections().get(sectionId).getTotalLessons();

    //         courseResponse.get(courseId).getSections().get(sectionId).setTotalLessons(currentTotalLessons + 1);

    //         if(status.equals("Completed")) {
    //             int currentCompletedLessons = courseResponse.get(courseId).getSections().get(sectionId).getCompletedLessons();

    //             courseResponse.get(courseId).getSections().get(sectionId).setCompletedLessons(currentCompletedLessons + 1);
    //         }
            
    //         courseResponse.get(courseId).getSections().get(sectionId).getLessons().put(lessonId, status);
    //     }

    //     for (String courseKey : courseResponse.keySet()) {
    //         for(String  sectionKey : courseResponse.get(courseKey).getSections().keySet()) {
    //             int completedLessons = courseResponse.get(courseKey).getSections().get(sectionKey).getCompletedLessons();
    //             int totalLessons = courseResponse.get(courseKey).getSections().get(sectionKey).getTotalLessons();

    //             if(completedLessons == totalLessons) {
    //                 courseResponse.get(courseKey).getSections().get(sectionKey).setStatus("Completed");

    //                 int currentCompletedSections = courseResponse.get(courseKey).getCompletedSections();

    //                 courseResponse.get(courseKey).setCompletedSections(currentCompletedSections + 1);
    //             }
    //         }

    //         int completedSections = courseResponse.get(courseKey).getCompletedSections();
    //         int totalSections = courseResponse.get(courseKey).getTotalSections();

    //         if(completedSections == totalSections) {
    //             courseResponse.get(courseKey).setStatus("Completed");
    //         }

    //         courseResponse.get(courseKey).setProgressPercentage(completedSections/totalSections * 100);
    //     }

    //     return courseResponse;

    // }

}
