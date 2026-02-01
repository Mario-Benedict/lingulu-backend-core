package com.lingulu.service;

import com.lingulu.entity.Lesson;
import com.lingulu.entity.LessonProgress;
import com.lingulu.entity.User;
import com.lingulu.enums.ProgressStatus;
import com.lingulu.repository.LessonProgressRepository;
import com.lingulu.repository.LessonRepository;
import com.lingulu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final UserRepository userRepository;

    public void enrollUserToAllLessons(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Lesson> lessons = lessonRepository.findAll();

        for (Lesson lesson : lessons) {
            LessonProgress progress = LessonProgress.builder()
                    .user(user)
                    .lesson(lesson)
                    .status(ProgressStatus.valueOf("IN_PROGRESS"))
                    .build();

            lessonProgressRepository.save(progress);
        }
    }
}
