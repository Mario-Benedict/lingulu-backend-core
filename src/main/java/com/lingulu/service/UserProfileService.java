package com.lingulu.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.dto.ProfileResponse;
import com.lingulu.repository.CourseProgressRepository;
import com.lingulu.repository.LeaderboardRepository;
import com.lingulu.repository.UserLearningStatsRepository;
import com.lingulu.repository.UserProfileRepository;
import com.lingulu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;

    public ProfileResponse getUserProfile(UUID userId){
        ProfileResponse profileResponse = userRepository.getUserProfile(userId);

        return profileResponse;
    }
}
