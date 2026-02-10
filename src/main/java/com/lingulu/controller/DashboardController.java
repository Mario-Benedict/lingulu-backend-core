package com.lingulu.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.DashboardResponse;
import com.lingulu.service.DashboardService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;


@Validated
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<?>> getDashboard() {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        DashboardResponse dashboardResponse = dashboardService.getDashboard(UUID.fromString(userId));

        return ResponseEntity.ok()
            .body(new ApiResponse<>(true, "Dashboard data recieved", dashboardResponse));
    }
    
}
