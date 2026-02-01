package com.lingulu.controller;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.LoginRequest;
import com.lingulu.dto.RegisterRequest;
import com.lingulu.dto.UserResponse;
import com.lingulu.entity.User;
import com.lingulu.repository.UserRepository;
import com.lingulu.security.JwtUtil;
import com.lingulu.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @Value("${spring.application.dev}")
    private Boolean isDev;

    private String generateCookie(String token) {
        return ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(!isDev)
                .sameSite("Lax")
                .path("/")
                .maxAge(7*24*60*60)
                .build()
                .toString();
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
        @Valid @RequestBody RegisterRequest request
    ) throws Exception {
        User user = authService.register(request);

        String token = jwtUtil.generateAccessToken(user);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, generateCookie(token))
            .body(new ApiResponse<>(true, "Registration successful",
                    UserResponse
                        .builder()
                        .accessToken(token)
                        .build()
            ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) throws Exception {
        String token = authService.login(request);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, generateCookie(token))
            .body(new ApiResponse<>(true, "Login successful",
                    UserResponse
                            .builder()
                            .accessToken(token)
                            .build()
            ));
    }

    @GetMapping("/authenticated")
    public ResponseEntity<ApiResponse<Boolean>> isAuthenticated(
        @CookieValue(name = "token", required = false) String token
    ) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse<Boolean>(true, "Token validation result", false)
            );
        }

        boolean isValid = jwtUtil.validateToken(token);

        return ResponseEntity.ok(
                new ApiResponse<Boolean>(true, "Token validation result", isValid)
        );
    }
}
