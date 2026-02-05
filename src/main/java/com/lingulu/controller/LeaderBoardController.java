package com.lingulu.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.LeaderboardResponse;
import com.lingulu.entity.Leaderboard;
import com.lingulu.service.LeaderboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderBoardController {
    
    private final LeaderboardService leaderboardService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<LeaderboardResponse>>> leaderboard() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();


        List<LeaderboardResponse> leaderboardResponse = leaderboardService.getTop10Leaderboards();
        
        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Top 10 Leaderboard recieved successfully", leaderboardResponse));
    }

    @GetMapping("/user-rank")
    public ResponseEntity<ApiResponse<?>> userRank() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();


        LeaderboardResponse response = leaderboardService.getUserRank(UUID.fromString(userId));
        
        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "User Rank recieved", response));
    }
    
    
}
