package com.lingulu.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.entity.account.User;
import com.lingulu.entity.account.UserLearningStats;
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
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        } else if (lastActivityDate.isBefore(today.minusDays(1))) {
            stats.setCurrentStreak(1);
        } else if(lastActivityDate.isEqual(today) && stats.getCurrentStreak() == 0) {
            stats.setCurrentStreak(1);
        }
        if (stats.getCurrentStreak() > stats.getLongestStreak()) {
            stats.setLongestStreak(stats.getCurrentStreak());
        }

        stats.setLastActivityDate(today);

        userLearningStatsRepository.save(stats);
    }
}
