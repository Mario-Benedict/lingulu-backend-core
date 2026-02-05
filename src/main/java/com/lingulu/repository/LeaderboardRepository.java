package com.lingulu.repository;

import com.lingulu.dto.LeaderboardResponse;
import com.lingulu.entity.Leaderboard;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {
    @Query("""
        SELECT new com.lingulu.dto.LeaderboardResponse(
            u.userId,
            up.username,
            l.totalPoints,
            l.rank
        )
        FROM Leaderboard l
        JOIN l.user u
        JOIN u.userProfile up
        ORDER BY l.totalPoints DESC
    """)
    List<LeaderboardResponse> findTopLeaderboard(PageRequest pageable);

    Leaderboard findByUser_UserId(UUID userId);

    @Query("""
        SELECT new com.lingulu.dto.LeaderboardResponse(
            u.userId,
            up.username,
            l.totalPoints,
            l.rank
        )
        FROM Leaderboard l
        JOIN l.user u
        JOIN u.userProfile up
        WHERE u.userId = :userId
    """)
    LeaderboardResponse findUserRank(UUID userId);
}
