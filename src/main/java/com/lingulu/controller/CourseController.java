package com.lingulu.controller;

import com.lingulu.dto.ApiResponse;
import com.lingulu.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ApiResponse<?> getCourses() {
        return new ApiResponse<>(true, "Course list",
                courseService.getAllCourses());
    }
}
