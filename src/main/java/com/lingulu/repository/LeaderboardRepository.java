

package com.lingulu.repository;

import com.lingulu.dto.LeaderboardResponse;
import com.lingulu.entity.Leaderboard;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {
    @Query("""
        SELECT new com.lingulu.dto.LeaderboardResponse(
            up.username,
            l.totalPoints
        )
        FROM Leaderboard l
        JOIN l.user u
        JOIN u.userProfile up
        ORDER BY l.totalPoints DESC
    """)
    List<LeaderboardResponse> findTopLeaderboard(Pageable pageable);


    @Query("""
        SELECT new  com.lingulu.dto.LeaderboardResponse(
            up.username,
            l.totalPoints
        )        
        FROM Leaderboard l
        JOIN l.user u
        JOIN u.userProfile up
        WHERE u.userId = :userId
    """)
    LeaderboardResponse findByUserId(@Param("userId") UUID userId);

    Leaderboard findByUser_UserId(UUID userId);
}