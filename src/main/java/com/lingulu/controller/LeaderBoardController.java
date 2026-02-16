package com.lingulu.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.response.general.ApiResponse;
import com.lingulu.dto.response.info.LeaderboardResponse;
import com.lingulu.dto.response.info.UserRankResponse;
import com.lingulu.service.CloudFrontSigner;
import com.lingulu.service.LeaderboardService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCustomPolicy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpHeaders;



@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderBoardController {
    
    private final LeaderboardService leaderboardService;
    private final CloudFrontSigner cloudFrontSigner;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<LeaderboardResponse>>> leaderboard() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();


        List<LeaderboardResponse> leaderboardResponse = leaderboardService.getTop10Leaderboards();

        leaderboardResponse.forEach(lr ->
                lr.setAvatarUrl(cloudFrontSigner.generateSignedUrl(lr.getAvatarUrl()))
        );

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Top 10 Leaderboard received successfully", leaderboardResponse));
    }

    @GetMapping("/user-rank")
    public ResponseEntity<ApiResponse<UserRankResponse>> userRank() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        UserRankResponse response = leaderboardService.getUserRank(UUID.fromString(userId));
        String presignedUrl = cloudFrontSigner.generateSignedUrl(response.getAvatarUrl());
        response.setAvatarUrl(presignedUrl);

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "User Rank received", response));
    }
    
    
}
