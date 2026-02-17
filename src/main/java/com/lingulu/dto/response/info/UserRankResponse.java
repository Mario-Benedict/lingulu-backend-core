package com.lingulu.dto.response.info;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRankResponse {
    private UUID userId;
    private String username;
    private long rank;
    private int totalPoints;
    private String avatarUrl;
}
