package com.lingulu.dto.response.info;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String username;
    private String email;
    private String avatarUrl;
    private int streak;
    private int totalPoints;
    private long rank;
    private Long completedSections;
    private String bio;
}
