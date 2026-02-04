package com.lingulu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.ProfileResponse;
import com.lingulu.service.UserProfileService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getMethodName() {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        ProfileResponse profileResponse = userProfileService.getUserProfile(UUID.fromString(userId));

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "User profile recieved successfully", profileResponse));
    }
}
