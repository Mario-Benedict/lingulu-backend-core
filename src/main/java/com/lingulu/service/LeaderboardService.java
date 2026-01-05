package com.lingulu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lingulu.dto.ApiResponse;
import com.lingulu.entity.Leaderboard;
import com.lingulu.entity.User;
import com.lingulu.repository.LeaderboardRepository;
import com.lingulu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    
    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;

    public List<Leaderboard> getTop10Leaderboards(UUID userId) {

        User user = userRepository.findByUserId(userId).orElse(null);

        List<Leaderboard> leaderboards = leaderboardRepository.findTop10ByOrderByTotalPointsDesc();

        Leaderboard leaderboardUser = leaderboardRepository.findByUser_UserId(user.getUserId());

        if(leaderboards.contains(leaderboardUser)){
            return leaderboards;
        } else {
            leaderboards.add(leaderboardUser);
            return leaderboards;
        }
    }

    public void addLeaderBoard(User user) {
        Leaderboard leaderboard = Leaderboard.builder()
                                    .user(user)
                                    .totalPoints(0)
                                    .build();
        leaderboardRepository.save(leaderboard);
    }
        
    public ResponseEntity<ApiResponse<?>> response(List<Leaderboard> leaderboards) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Top 10 Leaderboards retrieved successfully", leaderboards)
        );
    }
}
