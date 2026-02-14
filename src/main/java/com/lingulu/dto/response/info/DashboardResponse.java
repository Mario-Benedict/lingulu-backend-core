package com.lingulu.dto.response.info;

import com.lingulu.dto.response.course.CourseResponse;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private CourseResponse courseResponse;
    private String username;
    private int streak;
    private long rank;
}
