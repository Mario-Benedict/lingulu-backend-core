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
}
