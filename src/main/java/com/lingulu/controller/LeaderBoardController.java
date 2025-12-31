package com.lingulu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.ApiResponse;
import com.lingulu.entity.Leaderboard;
import com.lingulu.service.LeaderboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeaderBoardController {
    
    private final LeaderboardService leaderboardService;

    @PostMapping("/leaderboard")
    public ResponseEntity<ApiResponse<?>> leaderboard() {


        // List<Leaderboard> leaderboards = leaderboardService.getTop10Leaderboards(userId);
        
        return leaderboardService.response(null);
    }
    
}
