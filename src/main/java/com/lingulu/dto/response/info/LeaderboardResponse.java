package com.lingulu.dto.response.info;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {
    private UUID userId;
    private String username;
    private int totalPoints;
    private String avatarUrl;
}
