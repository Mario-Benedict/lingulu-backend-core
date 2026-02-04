package com.lingulu.service;

import com.lingulu.dto.UserProfileResponse;
import com.lingulu.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserProfileRepository userProfileRepository;

    // public UserProfileResponse getUserProfile(UUID userId) {
    //     return userProfileRepository.findActiveProfileByUserId(userId);
    // }
}
