package com.lingulu.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.dto.response.course.CourseResponse;
import com.lingulu.dto.response.info.DashboardResponse;
import com.lingulu.entity.CourseProgress;
import com.lingulu.enums.ProgressStatus;
import com.lingulu.repository.CourseProgressRepository;
import com.lingulu.repository.LeaderboardRepository;
import com.lingulu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final LeaderboardRepository leaderboardRepository;

    public DashboardResponse getDashboard(UUID userId){
        CourseProgress courseProgress = courseProgressRepository.findActiveCourse(userId, ProgressStatus.IN_PROGRESS);
        float progressPercentage = courseProgress.getCompletedLessons() * 100 / courseProgress.getTotalLessons();
        CourseResponse courseResponse = CourseResponse.builder()
                                        .courseId(courseProgress.getCourse().getCourseId())
                                        .courseTitle(courseProgress.getCourse().getCourseTitle())
                                        .status(String.valueOf(courseProgress.getStatus()))
                                        .completedSections(courseProgress.getCompletedLessons())
                                        .totalSections(courseProgress.getTotalLessons())
                                        .progressPercentage(progressPercentage)
                                        .build();
        
        DashboardResponse dashboardResponse = userRepository.getDashboardUser(userId);
        int rank = leaderboardRepository.getUserRank(userId);
        dashboardResponse.setRank(rank);
        dashboardResponse.setCourseResponse(courseResponse);

        return dashboardResponse;
    }
}
