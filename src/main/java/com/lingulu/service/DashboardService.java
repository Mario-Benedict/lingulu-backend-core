package com.lingulu.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lingulu.dto.DashboardResponse;
import com.lingulu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;

    public DashboardResponse getDashboard(UUID userid){
        DashboardResponse dashboardResponse = userRepository.getDashboard(userid);

        return dashboardResponse;
    }
}
