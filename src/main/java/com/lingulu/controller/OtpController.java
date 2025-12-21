package com.lingulu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.service.AuthService;
import com.lingulu.service.OtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final AuthService authService;

    @PostMapping("/request")
    public ResponseEntity<?> requestOtp(@RequestParam String email) {
        otpService.sendOtp(email);
        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp
    ) {
        otpService.verifyOtp(email, otp);
        authService.setEmailVerified(email);
        return ResponseEntity.ok("OTP verified");
    }
}
