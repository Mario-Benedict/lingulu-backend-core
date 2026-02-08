package com.lingulu.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lingulu.dto.ProfileResponse;
import com.lingulu.entity.UserProfile;
import com.lingulu.repository.UserProfileRepository;
import com.lingulu.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final S3StorageService s3StorageService;
    private final UserRepository userRepository;

    public ProfileResponse getUserProfile(UUID userId){
        ProfileResponse profileResponse = userRepository.getUserProfile(userId);

        return profileResponse;
    }

    private static final List<String> DEFAULT_AVATARS = List.of(
        "avatars/tiger1.webp",
        "avatars/tiger2.webp",
        "avatars/tiger3.webp",
        "avatars/tiger4.webp"
    );

    public String getAvatarUrl(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUser_UserId(userId);
        return userProfile.getAvatarUrl();
    }

    public String pickAvatarByUserId() {
        int index = ThreadLocalRandom.current().nextInt(DEFAULT_AVATARS.size());
        return DEFAULT_AVATARS.get(index);
    }    

    public String updateAvatar(MultipartFile file, UUID userId) throws IOException {
        String filename = file.getOriginalFilename();
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        String s3Key = "users/avatars/" + userId + "/avatar." + ext;

        UserProfile userProfile = userProfileRepository.findByUser_UserId(userId);
        userProfile.setAvatarUrl(s3Key);

        s3StorageService.uploadMultipartFile(file, s3Key, "profile");

        userProfileRepository.save(userProfile);
        
        return s3Key;
    }

    public void updateBio(String bio, UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUser_UserId(userId);
        userProfile.setBio(bio);
        userProfileRepository.save(userProfile);
    }
}
