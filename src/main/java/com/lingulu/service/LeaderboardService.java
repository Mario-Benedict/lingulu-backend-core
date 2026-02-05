package com.lingulu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.LeaderboardResponse;
import com.lingulu.entity.Leaderboard;
import com.lingulu.entity.User;
import com.lingulu.exception.AppException;
import com.lingulu.exception.DataNotFoundException;
import com.lingulu.repository.LeaderboardRepository;
import com.lingulu.repository.UserRepository;
import com.lingulu.exception.AppException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    
    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;

    public List<LeaderboardResponse> getTop10Leaderboards() {
        return leaderboardRepository.findTopLeaderboard(PageRequest.of(0, 10));
    }

    public void addLeaderBoard(User user) {
        Leaderboard leaderboard = Leaderboard.builder()
                                    .user(user)
                                    .totalPoints(0)
                                    .rank(leaderboardRepository.count() + 1)
                                    .build();
        leaderboardRepository.save(leaderboard);
    }

    public void updateTotalPoints(UUID userId) {
        Leaderboard leaderboard = leaderboardRepository.findByUser_UserId(userId);

        if (leaderboard != null) {
            leaderboard.setTotalPoints(leaderboard.getTotalPoints() + 100);
            leaderboardRepository.save(leaderboard);
        }
        else {
            throw new DataNotFoundException("Leaderboard user not found", HttpStatus.NOT_FOUND);
        }
    }
}
