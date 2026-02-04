package com.lingulu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.CdnAccessResponse;
import com.lingulu.dto.UploadAvatarRequest;
import com.lingulu.service.CloudFrontSigner;
import com.lingulu.service.UserProfileService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class ProfileController {
    @Value("${spring.application.dev}")
    private Boolean isDev;

    private final UserProfileService userProfileService;
    private final CloudFrontSigner cloudFrontSigner;

    
    @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> uploadAvatar(@Valid @ModelAttribute UploadAvatarRequest request) throws IOException {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        String s3Key = userProfileService.updateAvatar(request.getAvatarFile(), UUID.fromString(userId));
        String fullCdnUrl = cloudFrontSigner.generateCdnUrl(s3Key);
        CdnAccessResponse cdnUrl = CdnAccessResponse.builder()
                                    .avatarUrl(fullCdnUrl)
                                    .build();

        CookiesForCannedPolicy signedCookies = cloudFrontSigner.generateSignedCookies(fullCdnUrl);

        // Buat objek ResponseCookie untuk Signature
        ResponseCookie signatureCookie = ResponseCookie.from("CloudFront-Signature", signedCookies.signatureHeaderValue())
                .httpOnly(true)
                .secure(true) // Wajib jika menggunakan CloudFront HTTPS
                .path("/")
                .sameSite("Lax")
                .build();

        // Buat objek ResponseCookie untuk Key-Pair-Id
        ResponseCookie keyPairCookie = ResponseCookie.from("CloudFront-Key-Pair-Id", signedCookies.keyPairIdHeaderValue())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, signatureCookie.toString())
            .header(HttpHeaders.SET_COOKIE, keyPairCookie.toString())
            .body(new ApiResponse<>(true, "Avatar updated", cdnUrl));
        
    }
    
}