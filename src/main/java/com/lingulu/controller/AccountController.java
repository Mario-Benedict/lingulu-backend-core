package com.lingulu.controller;

import com.lingulu.dto.request.account.*;
import com.lingulu.dto.request.profile.ChangePasswordRequest;
import com.lingulu.dto.response.general.ApiResponse;
import com.lingulu.dto.response.account.AuthenticationResponse;
import com.lingulu.entity.account.User;
import com.lingulu.repository.UserRepository;
import com.lingulu.security.JwtUtil;
import com.lingulu.service.AccountService;
import com.lingulu.service.AuthService;
import com.lingulu.service.PasswordResetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AuthService authService;
    private final AccountService accountService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;

    @Value("${spring.application.dev}")
    private Boolean isDev;

    private String generateCookie(String token, Boolean isRememberMe) {
        int days = isRememberMe ? 7 : 1;

        return ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
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
        boolean isValid = (token != null && !token.isEmpty()) && jwtUtil.validateToken(token);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Invalid token",
                            AuthenticationResponse.builder().authenticated(false).build()));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Token is valid",
                AuthenticationResponse.builder().authenticated(true).build()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
        @Valid @RequestBody ForgotPasswordRequest request
    ) {
        passwordResetService.sendPasswordResetEmail(request.getEmail());

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Password reset link has been sent to your email", null)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
        @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.resetPassword(request.getToken(), request.getPassword());

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Password has been reset successfully", null)
        );
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<ApiResponse<Boolean>> validateResetToken(
        @RequestParam String token
    ) {
        boolean isValid = passwordResetService.validateResetToken(token);

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Token validation result", isValid)
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        accountService.changePassword(
            UUID.fromString(userId),
            request.getCurrentPassword(),
            request.getNewPassword()
        );

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Password has been changed successfully", null)
        );
    }
}
