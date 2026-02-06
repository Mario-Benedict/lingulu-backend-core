package com.lingulu.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.entity.User;
import com.lingulu.entity.UserLearningStats;
import com.lingulu.repository.UserLearningStatsRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserLearningStatsService {
    private final UserLearningStatsRepository userLearningStatsRepository;

    public void addUserLearningStats(User user) {
        UserLearningStats userLearningStats = UserLearningStats.builder()
                                                .user(user)
                                                .currentStreak(0)
                                                .longestStreak(0)
                                                .lastActivityDate(LocalDate.now())
                                                .build();
    
        userLearningStatsRepository.save(userLearningStats);

    }

    public void updateStreak(UUID userId) {
        UserLearningStats stats = userLearningStatsRepository.findByUser_UserId(userId);
        LocalDate today = LocalDate.now();
        LocalDate lastActivityDate = stats.getLastActivityDate();

        if (lastActivityDate.isEqual(today.minusDays(1))) {
            // Increment streak
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        } else if (lastActivityDate.isBefore(today.minusDays(1))) {
            // Reset streak
            stats.setCurrentStreak(1);
        } else if(lastActivityDate.isEqual(today) && stats.getCurrentStreak() == 0) {
            // First Avtivity after create account
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        }
        // Update longest streak if needed
        if (stats.getCurrentStreak() > stats.getLongestStreak()) {
            stats.setLongestStreak(stats.getCurrentStreak());
        }

        // Update last activity date
        stats.setLastActivityDate(today);

        userLearningStatsRepository.save(stats);
    }
}
