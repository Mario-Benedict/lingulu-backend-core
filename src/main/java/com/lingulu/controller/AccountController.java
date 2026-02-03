package com.lingulu.controller;

import com.lingulu.dto.*;
import com.lingulu.entity.User;
import com.lingulu.entity.UserProfile;
import com.lingulu.repository.UserRepository;
import com.lingulu.security.JwtUtil;
import com.lingulu.service.AccountService;
import com.lingulu.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AuthService authService;
    private final AccountService accountService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${spring.application.dev}")
    private Boolean isDev;

    private String generateCookie(String token, Boolean isRememberMe) {
        int days = isRememberMe ? 7 : 1;

        return ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(!isDev)
                .sameSite("Lax")
                .path("/")
                .maxAge(days * 24 * 60 * 60)
                .build()
                .toString();
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
        @Valid @RequestBody RegisterRequest request
    ) throws Exception {
        User user = authService.register(request);

        String token = jwtUtil.generateAccessToken(user);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, generateCookie(token, true))
            .body(new ApiResponse<>(true, "Registration successful",
                    AuthenticationResponse
                        .builder()
                        .authenticated(true)
                        .build()
            ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) throws Exception {
        String token = authService.login(request);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, generateCookie(token, request.getIsRememberMe()))
            .body(new ApiResponse<>(true, "Login successful",
                    AuthenticationResponse
                        .builder()
                        .authenticated(true)
                        .build()
            ));
    }

    @GetMapping("/authenticated")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> isAuthenticated(
        @CookieValue(name = "token", required = false) String token
    ) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Token validation result",
                    AuthenticationResponse
                        .builder()
                        .authenticated(false)
                        .build()
                )
            );
        }

        boolean isValid = jwtUtil.validateToken(token);

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Token validation result",
                AuthenticationResponse
                    .builder()
                    .authenticated(true)
                    .build()
            )
        );
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> profile() {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal());

        UserProfileResponse profile = accountService.getUserProfile(userId);

        return ResponseEntity.ok(
            new ApiResponse<UserProfileResponse>(true, "User profile fetched successfully",
                UserProfileResponse.builder()
                    .username(profile.getUsername())
                    .avatarUrl(profile.getAvatarUrl())
                    .bio(profile.getBio())
                    .preferredLanguage(profile.getPreferredLanguage())
                    .audioPath(profile.getAudioPath())
                    .build()
            )
        );
    }
}
