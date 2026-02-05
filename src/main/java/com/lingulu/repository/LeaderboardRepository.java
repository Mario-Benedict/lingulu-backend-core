package com.lingulu.repository;

import com.lingulu.dto.LeaderboardResponse;
import com.lingulu.dto.UserRankResponse;
import com.lingulu.entity.Leaderboard;

import io.lettuce.core.dynamic.annotation.Param;

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
            l.totalPoints
        )
        FROM Leaderboard l
        JOIN l.user u
        JOIN u.userProfile up
        ORDER BY l.totalPoints DESC
    """)
    List<LeaderboardResponse> findTopLeaderboard(PageRequest pageable);

    Leaderboard findByUser_UserId(UUID userId);

    @Query("""
        SELECT new com.lingulu.dto.UserRankResponse(
            u.userId,
            up.username,
            0,
            l.totalPoints
        )
        FROM Leaderboard l
        JOIN l.user u
        JOIN u.userProfile up
        WHERE u.userId = :userId
    """)
    UserRankResponse findUserRank(UUID userId);

    @Query(value = """
        SELECT 1 + COUNT(*) AS rank
        FROM leaderboard lb
        WHERE lb.total_points > (
            SELECT total_points
            FROM leaderboard
            WHERE user_id = :userId
        )
    """, nativeQuery = true)
    Integer getUserRank(@Param("userId") UUID userId);

}
