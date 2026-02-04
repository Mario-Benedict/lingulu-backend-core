package com.lingulu.repository;

import com.lingulu.dto.LeaderboardResponse;
import com.lingulu.entity.Leaderboard;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

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
    List<LeaderboardResponse> findTopLeaderboard(Pageable pageable);


    @Query("""
        SELECT new  com.lingulu.dto.LeaderboardResponse(
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
    LeaderboardResponse findByUserId(@Param("userId") UUID userId);

    Leaderboard findByUser_UserId(UUID userId);
}
