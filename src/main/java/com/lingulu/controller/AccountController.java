package com.lingulu.controller;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.LoginRequest;
import com.lingulu.dto.RegisterRequest;
import com.lingulu.entity.User;
import com.lingulu.repository.UserRepository;
import com.lingulu.security.JwtUtil;
import com.lingulu.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);

        return authService.response(user);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);

       return authService.response(user);
    }

    @GetMapping("/oauth2/data")
    public ResponseEntity<ApiResponse<?>> verifyOAuthLogin(@CookieValue(value = "oauth_token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse<>(false, "No OAuth token found", null));
        }

        try {
            // Verify token dan get user data
            UUID userId = UUID.fromString(jwtUtil.getUserIdFromToken(token));
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // UserResponse userResponse = UserResponse.builder()
            //     .userId(user.getUserId())
            //     .email(user.getEmail())
            //     .accessToken(token)
            //     .build();

            // return ResponseEntity.ok(new ApiResponse<>(true, "Login berhasil", userResponse));
            return authService.response(user);
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(new ApiResponse<>(false, "Invalid token", null));
        }
    }
}
