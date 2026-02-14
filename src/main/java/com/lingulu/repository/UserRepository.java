package com.lingulu.repository;

import com.lingulu.dto.response.info.DashboardResponse;
import com.lingulu.dto.response.info.ProfileResponse;
import com.lingulu.entity.account.User;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(UUID userId);

    // Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        SELECT new com.lingulu.dto.response.info.ProfileResponse(
            up.username,
            u.email,
            up.avatarUrl,
            ul.currentStreak,
            lb.totalPoints,
            CAST(0 AS long),
            CAST(COALESCE(SUM(lp.completedSections), 0) AS long),
            up.bio
        )
        FROM User u
        JOIN u.userProfile up
        JOIN u.userLearningStats ul
        JOIN u.leaderboard lb
        LEFT JOIN u.lessonProgress lp
        WHERE u.userId = :userId
        GROUP BY
            u.email,
            up.username,
            up.avatarUrl,
            ul.currentStreak,
            lb.totalPoints,
            up.bio
    """)
    ProfileResponse getUserProfile(@Param("userId") UUID userId);

    @Query("""
        SELECT new com.lingulu.dto.response.info.DashboardResponse(
            null,
            up.username,
            ul.currentStreak,
            0L
        )
        FROM User u
        JOIN u.userProfile up
        JOIN u.userLearningStats ul
        WHERE u.userId = :userId
    """)
    DashboardResponse getDashboardUser(@Param("userId") UUID userId);
}