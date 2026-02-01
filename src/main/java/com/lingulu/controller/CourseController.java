package com.lingulu.controller;

import com.lingulu.dto.CourseResponse;
import com.lingulu.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/my")
    public List<CourseResponse> getMyCourses(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());
        return courseService.getMyCourses(userId);
    }
}
