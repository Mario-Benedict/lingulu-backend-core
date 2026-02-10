package com.lingulu.service;

import com.lingulu.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserProfileRepository userProfileRepository;

    // public UserProfileResponse getUserProfile(UUID userId) {
    //     return userProfileRepository.findActiveProfileByUserId(userId);
    // }
}
