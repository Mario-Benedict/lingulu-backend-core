package com.lingulu.controller;

import com.lingulu.dto.request.account.OtpRequest;
import com.lingulu.dto.request.account.OtpVerifyRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> requestOtp(
        @Valid
        @RequestBody
        OtpRequest otpRequest
    ) throws Exception {
        otpService.sendOtp(otpRequest.getEmail());

        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            @Valid
            @RequestBody
            OtpVerifyRequest otpVerifyRequest
    ) throws Exception {
        String email = otpVerifyRequest.getEmail();
        String otp = otpVerifyRequest.getOtp();

        otpService.verifyOtp(email, otp);
        authService.setEmailVerified(email);

        return ResponseEntity.ok("OTP verified");
    }
}
