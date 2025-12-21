package com.lingulu.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lingulu.security.OtpGenerator;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OtpService {

    private static final String OTP_KEY_PREFIX = "otp:email:";
    private static final Duration OTP_TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final OtpGenerator otpGenerator;
    private final EmailService emailService;

    public void sendOtp(String email) {
        String otp = otpGenerator.generate();
        String hashedOtp = passwordEncoder.encode(otp);

        String redisKey = OTP_KEY_PREFIX + email;

        redisTemplate.opsForValue()
            .set(redisKey, hashedOtp, OTP_TTL);

        emailService.sendOtp(email, otp);
    }

    public void verifyOtp(String email, String inputOtp) {
        String redisKey = OTP_KEY_PREFIX + email;

        String storedHash = redisTemplate.opsForValue().get(redisKey);

        if (storedHash == null) {
            throw new RuntimeException("OTP expired or not found");
        }

        if (!passwordEncoder.matches(inputOtp, storedHash)) {
            throw new RuntimeException("Invalid OTP");
        }

        // OTP sekali pakai
        redisTemplate.delete(redisKey);
    }
}

