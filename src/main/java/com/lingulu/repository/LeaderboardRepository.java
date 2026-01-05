package com.lingulu.repository;

import com.lingulu.entity.Leaderboard;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {
    List<Leaderboard> findTop10ByOrderByTotalPointsDesc();

    Leaderboard findByUser_UserId(UUID userId);
}
