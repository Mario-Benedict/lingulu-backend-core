package com.lingulu.dto.response.course;

import java.util.Map;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteCourseResponse {
    Map<String, String> listCourses;
    String status;
}
