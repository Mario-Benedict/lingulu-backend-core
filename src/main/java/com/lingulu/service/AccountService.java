package com.lingulu.service;

import com.lingulu.entity.account.User;
import com.lingulu.exception.UserNotFoundException;
import com.lingulu.repository.UserProfileRepository;
import com.lingulu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new UserNotFoundException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }

        // Check if new password is same as current
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new UserNotFoundException("New password must be different from current password", HttpStatus.BAD_REQUEST);
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // public UserProfileResponse getUserProfile(UUID userId) {
    //     return userProfileRepository.findActiveProfileByUserId(userId);
    // }
}
