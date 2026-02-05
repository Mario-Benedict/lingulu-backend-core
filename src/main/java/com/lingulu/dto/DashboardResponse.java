package com.lingulu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DashboardResponse {
    private CourseResponse courseResponse;
    private String username;
    private int streak;
    private long rank;
}
