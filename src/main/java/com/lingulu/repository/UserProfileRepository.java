package com.lingulu.repository;

import com.lingulu.entity.UserProfile;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    boolean existsByUsername(String username);
}
