package com.lingulu.repository;

import com.lingulu.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    boolean existsByUsername(String username);
}
