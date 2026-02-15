package com.lingulu.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.dto.response.course.CourseResponse;
import com.lingulu.dto.response.info.DashboardResponse;
import com.lingulu.entity.account.User;
import com.lingulu.entity.account.UserLearningStats;
import com.lingulu.entity.course.CourseProgress;
import com.lingulu.enums.ProgressStatus;
import com.lingulu.repository.CourseProgressRepository;
import com.lingulu.repository.LeaderboardRepository;
import com.lingulu.repository.UserLearningStatsRepository;
import com.lingulu.repository.UserProfileRepository;
import com.lingulu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UserLearningStatsRepository userLearningStatsRepository;
    private final UserProfileRepository userProfileRepository;

    public DashboardResponse getDashboard(UUID userId){
        CourseProgress courseProgress = courseProgressRepository.findActiveCourse(userId, ProgressStatus.IN_PROGRESS);
        int streak = userLearningStatsRepository.getStreak(userId);
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
        String username = userProfileRepository.getUsername(userId);
        dashboardResponse.setStreak(streak);
        dashboardResponse.setRank(rank);
        dashboardResponse.setUsername(username);
        dashboardResponse.setCourseResponse(courseResponse);

        return dashboardResponse;
    }
}
