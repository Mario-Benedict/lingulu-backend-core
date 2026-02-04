package com.lingulu.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.UserLearningStats;

public interface UserLearningStatsRepository extends JpaRepository<UserLearningStats, UUID>{
    UserLearningStats findByUser_UserId(UUID userId);
}
