package com.lingulu.service;

import com.lingulu.entity.User;
import com.lingulu.exception.UserNotFoundException;
import com.lingulu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final String RESET_TOKEN_PREFIX = "password_reset:";
    private static final Duration TOKEN_TTL = Duration.ofHours(1);

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with this email not found", HttpStatus.NOT_FOUND));

        // Generate a unique reset token
        String resetToken = UUID.randomUUID().toString();
        String redisKey = RESET_TOKEN_PREFIX + resetToken;

        // Store user's email with the token
        redisTemplate.opsForValue().set(redisKey, email, TOKEN_TTL);

        // Send email with reset link
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        String redisKey = RESET_TOKEN_PREFIX + token;
        String email = redisTemplate.opsForValue().get(redisKey);

        if (email == null) {
            throw new UserNotFoundException("Invalid or expired reset token", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the used token
        redisTemplate.delete(redisKey);
    }

    public boolean validateResetToken(String token) {
        String redisKey = RESET_TOKEN_PREFIX + token;
        String email = redisTemplate.opsForValue().get(redisKey);
        return email != null;
    }
}
