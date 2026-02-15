package com.lingulu.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.dto.response.course.CourseResponse;
import com.lingulu.dto.response.info.DashboardResponse;
import com.lingulu.entity.course.CourseProgress;
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
        CourseProgress courseProgress = courseProgressRepository.findActiveCourse(userId, ProgressStatus.IN_PROGRESS)
                                        .orElseGet(() -> courseProgressRepository.findByUser_UserIdAndCourse_Position(userId, courseProgressRepository.countByUser_UserId(userId)));

        float progressPercentage = courseProgress.getCompletedLessons() * 100 / courseProgress.getTotalLessons();
        CourseResponse courseResponse = CourseResponse.builder()
                                        .courseId(courseProgress.getCourse().getCourseId())
                                        .courseTitle(courseProgress.getCourse().getCourseTitle())
                                        .status(String.valueOf(courseProgress.getStatus()))
                                        .completedLessons(courseProgress.getCompletedLessons())
                                        .totalLessons(courseProgress.getTotalLessons())
                                        .progressPercentage(progressPercentage)
                                        .build();
        
        DashboardResponse dashboardResponse = userRepository.getDashboardUser(userId);
        int rank = leaderboardRepository.getUserRank(userId);
        dashboardResponse.setRank(rank);
        dashboardResponse.setCourseResponse(courseResponse);

        return dashboardResponse;
    }
}
