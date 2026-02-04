package com.lingulu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DashboardResponse {
    private String difficultyLevel;
    private float progressPercentage;
    private int streak;
    private long rank;
}
