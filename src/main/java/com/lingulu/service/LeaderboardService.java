package com.lingulu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.lingulu.dto.response.info.LeaderboardResponse;
import com.lingulu.dto.response.info.UserRankResponse;
import com.lingulu.entity.Leaderboard;
import com.lingulu.entity.User;
import com.lingulu.exception.DataNotFoundException;
import com.lingulu.repository.LeaderboardRepository;
import com.lingulu.repository.UserLearningStatsRepository;
import com.lingulu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    
    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;
    private final UserLearningStatsRepository userLearningStatsRepository;

    public List<LeaderboardResponse> getTop10Leaderboards() {
        return leaderboardRepository.findTopLeaderboard(PageRequest.of(0, 10));
    }

    public void addLeaderBoard(User user) {
        Leaderboard leaderboard = Leaderboard.builder()
                                    .user(user)
                                    .totalPoints(0)
                                    .build();
        leaderboardRepository.save(leaderboard);
    }

    public void updateTotalPoints(UUID userId) {
        Leaderboard leaderboard = leaderboardRepository.findByUser_UserId(userId);

        if (leaderboard != null) {
            int streak = userLearningStatsRepository.getStreak(userId);
            leaderboard.setTotalPoints(leaderboard.getTotalPoints() + 100 * ( 1 + streak  / 50));
            leaderboardRepository.save(leaderboard);
        }
        else {
            throw new DataNotFoundException("Leaderboard user not found", HttpStatus.NOT_FOUND);
        }
    }

    public UserRankResponse getUserRank(UUID userId){
        UserRankResponse userRank = leaderboardRepository.findUserRank(userId);

        userRank.setRank(leaderboardRepository.getUserRank(userId));

        return userRank;
    }
}
