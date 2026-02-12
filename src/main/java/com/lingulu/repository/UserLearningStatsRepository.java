package com.lingulu.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lingulu.entity.account.UserLearningStats;

public interface UserLearningStatsRepository extends JpaRepository<UserLearningStats, UUID>{
    UserLearningStats findByUser_UserId(UUID userId);

    @Query("""
        SELECT uls.currentStreak
        FROM UserLearningStats uls
        JOIN uls.user u
        WHERE u.userId = :userId
    """)
    Integer getStreak(UUID userId);
}
