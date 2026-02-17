package com.lingulu.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import com.lingulu.dto.response.general.ApiResponse;
import com.lingulu.dto.response.general.CdnAccessResponse;
import com.lingulu.dto.response.info.ProfileResponse;
import com.lingulu.dto.request.profile.UpdateBioRequest;
import com.lingulu.dto.request.profile.UploadAvatarRequest;
import com.lingulu.service.CloudFrontSigner;
import com.lingulu.service.UserProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.UUID;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/account/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;
    private final CloudFrontSigner cloudFrontSigner;

    @GetMapping("")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMethodName() {
        String userId = (String) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();

        ProfileResponse profileResponse = userProfileService.getUserProfile(UUID.fromString(userId));
        String presignedURL = cloudFrontSigner.generateSignedUrl(profileResponse.getAvatarUrl());
        profileResponse.setAvatarUrl(presignedURL);

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "User profile recieved successfully", profileResponse));
    }

    @PatchMapping(value = "/update-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CdnAccessResponse>> uploadAvatar(@Valid @ModelAttribute UploadAvatarRequest request) throws IOException {
        String userId = (String)SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();

        String s3Key = userProfileService.updateAvatar(request.getAvatarFile(), UUID.fromString(userId));
        String presignedURL = cloudFrontSigner.generateSignedUrl(s3Key);
        CdnAccessResponse cdnUrl = CdnAccessResponse.builder()
                                    .avatarUrl(presignedURL)
                                    .build();

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Avatar updated", cdnUrl));

    }

    @GetMapping("/avatar-url")
    public ResponseEntity<ApiResponse<CdnAccessResponse>> getAvatarUrl() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        String s3Key = userProfileService.getAvatarUrl(UUID.fromString(userId));

        String presignedURL = cloudFrontSigner.generateSignedUrl(s3Key);

        CdnAccessResponse cdnUrl = CdnAccessResponse.builder()
                                    .avatarUrl(presignedURL)
                                    .build();

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Avatar updated", cdnUrl));

    }        

    @PatchMapping("/update-bio")
    public ResponseEntity<ApiResponse<?>> updateBio(@RequestBody @Valid UpdateBioRequest bio) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        userProfileService.updateBio(bio.getBio(), UUID.fromString(userId));
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Bio updated", null));
    }
    
}
