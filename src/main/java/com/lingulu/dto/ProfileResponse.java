package com.lingulu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String userName;
    private String email;
    private String avatarUrl;
    private int streak;
    private int totalPoints;
    private long rank;
    private Long completedLessons;
}
