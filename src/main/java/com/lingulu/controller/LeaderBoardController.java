package com.lingulu.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.ApiResponse;
import com.lingulu.entity.Leaderboard;
import com.lingulu.service.LeaderboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeaderBoardController {
    
    private final LeaderboardService leaderboardService;

    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<?>> leaderboard() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        System.out.println("userId: " + userId);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse<>(false, "Unauthorized", null));
        }

        List<Leaderboard> leaderboards = leaderboardService.getTop10Leaderboards(UUID.fromString(userId));
        
        return leaderboardService.response(leaderboards);
    }
    
}
