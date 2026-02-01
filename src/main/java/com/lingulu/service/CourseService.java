package com.lingulu.service;

import com.lingulu.dto.CourseResponse;
import com.lingulu.repository.CourseProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseProgressRepository courseProgressRepository;

    public List<CourseResponse> getMyCourses(UUID userId) {
        return courseProgressRepository.findByUser_UserId(userId)
                .stream()
                .map(CourseResponse::from)
                .toList();
    }
}
