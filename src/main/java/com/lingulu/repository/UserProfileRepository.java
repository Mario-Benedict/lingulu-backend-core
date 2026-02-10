package com.lingulu.repository;

import com.lingulu.dto.UserProfileResponse;
import com.lingulu.entity.UserProfile;

import java.util.UUID;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID>{

    boolean existsByUsername(String username);

    @Query("SELECT new com.lingulu.dto.UserProfileResponse(p.username, p.avatarUrl, p.bio) " +
            "FROM UserProfile p WHERE p.user.userId = :userId")
    UserProfileResponse findActiveProfileByUserId(@Param("userId") UUID userId);

    UserProfile findByUser_UserId(UUID userId);

}
