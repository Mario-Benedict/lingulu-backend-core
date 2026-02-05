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

    private HttpHeaders generateCookie(String cdnUrl){
        CookiesForCannedPolicy signedCookies = cloudFrontSigner.generateSignedCookies(cdnUrl);

        // Buat objek ResponseCookie untuk Signature
        ResponseCookie signatureCookie = ResponseCookie.from("CloudFront-Signature", signedCookies.signatureHeaderValue().split("=")[1])
                .httpOnly(true)
                .secure(true) // Wajib jika menggunakan CloudFront HTTPS
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("None")
                .build();

        // Buat objek ResponseCookie untuk Key-Pair-Id
        ResponseCookie keyPairCookie = ResponseCookie.from("CloudFront-Key-Pair-Id", signedCookies.keyPairIdHeaderValue().split("=")[1])
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("None")
                .build();

        ResponseCookie expiresCookie = ResponseCookie.from("CloudFront-Expires", signedCookies.expiresHeaderValue().split("=")[1])
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("None")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, signatureCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, keyPairCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, expiresCookie.toString());

        return headers;
    }
    

    @GetMapping("/profile/avatarUrl")
    public ResponseEntity<ApiResponse<?>> getAvatarUrl() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        String s3Key = userProfileService.getAvatarUrl(UUID.fromString(userId));

        String fullCdnUrl = cloudFrontSigner.generateCdnUrl(s3Key);
        CookiesForCannedPolicy signedCookies = cloudFrontSigner.generateSignedCookies(fullCdnUrl);

        CdnAccessResponse cdnUrl = CdnAccessResponse.builder()
                                    .avatarUrl(fullCdnUrl)
                                    .build();

        return ResponseEntity.ok()
            .headers(generateCookie(fullCdnUrl))
            .body(new ApiResponse<>(true, "Avatar updated", cdnUrl));

    }
    

    
    @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> uploadAvatar(@Valid @ModelAttribute UploadAvatarRequest request) throws IOException {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        String s3Key = userProfileService.updateAvatar(request.getAvatarFile(), UUID.fromString(userId));
        String fullCdnUrl = cloudFrontSigner.generateCdnUrl(s3Key);
        CdnAccessResponse cdnUrl = CdnAccessResponse.builder()
                                    .avatarUrl(fullCdnUrl)
                                    .build();

        return ResponseEntity.ok()
            .headers(generateCookie(fullCdnUrl))
            .body(new ApiResponse<>(true, "Avatar updated", cdnUrl));
        
    }
 
    
}