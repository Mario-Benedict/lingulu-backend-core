package com.lingulu.repository;

import com.lingulu.dto.ProfileResponse;
import com.lingulu.entity.User;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

import javax.swing.text.html.Option;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(UUID userId);

    // Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        SELECT new com.lingulu.dto.ProfileResponse(
            up.username,
            u.email,
            up.avatarUrl,
            ul.currentStreak,
            lb.totalPoints,
            lb.rank,
            COALESCE(SUM(sp.completedLessons), 0)
        )
        FROM User u
        JOIN u.userProfile up
        JOIN u.userLearningStats ul
        JOIN u.leaderboard lb
        LEFT JOIN u.sectionProgress sp
        WHERE u.userId = :userId
        GROUP BY
            u.email,
            up.username,
            up.avatarUrl,
            ul.currentStreak,
            lb.totalPoints,
            lb.rank
    """)
    ProfileResponse getUserProfile(@Param("userId") UUID userId);


}