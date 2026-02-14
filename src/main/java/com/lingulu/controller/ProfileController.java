package com.lingulu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.lingulu.dto.response.general.ApiResponse;
import com.lingulu.dto.response.general.CdnAccessResponse;
import com.lingulu.dto.response.info.ProfileResponse;
import com.lingulu.dto.request.profile.UpdateBioRequest;
import com.lingulu.dto.request.profile.UploadAvatarRequest;
import com.lingulu.service.CloudFrontSigner;
import com.lingulu.service.UserProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;

import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.UUID;
import org.springframework.http.MediaType;
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
    

    // get avatar url with cookies to access cdn
    // @GetMapping("/profile/avatarUrl")
    // public ResponseEntity<ApiResponse<?>> getAvatarUrl() {
    //     String userId = (String)SecurityContextHolder.getContext()
    //                    .getAuthentication().getPrincipal();
        
    //     String s3Key = userProfileService.getAvatarUrl(UUID.fromString(userId));

    //     String fullCdnUrl = cloudFrontSigner.generateCdnUrl(s3Key);

    //     CdnAccessResponse cdnUrl = CdnAccessResponse.builder()
    //                                 .avatarUrl(fullCdnUrl)
    //                                 .build();

    //     return ResponseEntity.ok()
    //         .headers(generateCookie(fullCdnUrl))
    //         .body(new ApiResponse<>(true, "Avatar updated", cdnUrl));

    // }
    

    // update avatar with cookies to access cdn
    // @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<ApiResponse<?>> uploadAvatar(@Valid @ModelAttribute UploadAvatarRequest request) throws IOException {
    //     String userId = (String)SecurityContextHolder.getContext()
    //                    .getAuthentication().getPrincipal();
        
    //     String s3Key = userProfileService.updateAvatar(request.getAvatarFile(), UUID.fromString(userId));
    //     String fullCdnUrl = cloudFrontSigner.generateCdnUrl(s3Key);
    //     CdnAccessResponse cdnUrl = CdnAccessResponse.builder()
    //                                 .avatarUrl(fullCdnUrl)
    //                                 .build();

    //     return ResponseEntity.ok()
    //         .headers(generateCookie(fullCdnUrl))
    //         .body(new ApiResponse<>(true, "Avatar updated", cdnUrl));
        
    // }

    // profile with cookies to access cdn
    // @GetMapping("/profile")
    // public ResponseEntity<ApiResponse<?>> getMethodName() {
    //     String userId = (String) SecurityContextHolder.getContext()
    //                    .getAuthentication().getPrincipal();
        
    //     ProfileResponse profileResponse = userProfileService.getUserProfile(UUID.fromString(userId));
    //     String fullCdnUrl = cloudFrontSigner.generateCdnUrl(profileResponse.getAvatarUrl());
    //     profileResponse.setAvatarUrl(fullCdnUrl);

    //     return ResponseEntity.ok()
    //         .headers(generateCookie(fullCdnUrl))
    //         .body(new ApiResponse<>(true, "User profile recieved successfully", profileResponse));
    // }

    // get profile with presignedURL
        @GetMapping("/profile")
        public ResponseEntity<ApiResponse<?>> getMethodName() {
            String userId = (String) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();
            
            ProfileResponse profileResponse = userProfileService.getUserProfile(UUID.fromString(userId));
            String presignedURL = cloudFrontSigner.generateSignedUrl(profileResponse.getAvatarUrl());
            profileResponse.setAvatarUrl(presignedURL);

            return ResponseEntity.ok()
                // .headers(generateCookie(fullCdnUrl))
                .body(new ApiResponse<>(true, "User profile recieved successfully", profileResponse));
        }

        @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApiResponse<?>> uploadAvatar(@Valid @ModelAttribute UploadAvatarRequest request) throws IOException {
            String userId = (String)SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();
            
            String s3Key = userProfileService.updateAvatar(request.getAvatarFile(), UUID.fromString(userId));
            String presignedURL = cloudFrontSigner.generateSignedUrl(s3Key);
            CdnAccessResponse cdnUrl = CdnAccessResponse.builder()
                                        .avatarUrl(presignedURL)
                                        .build();

            return ResponseEntity.ok()
                // .headers(generateCookie(cdnUrl))
                .body(new ApiResponse<>(true, "Avatar updated", cdnUrl));
            
        }

    @GetMapping("/profile/avatarUrl")
    public ResponseEntity<ApiResponse<?>> getAvatarUrl() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        String s3Key = userProfileService.getAvatarUrl(UUID.fromString(userId));

        String presignedURL = cloudFrontSigner.generateSignedUrl(s3Key);

        CdnAccessResponse cdnUrl = CdnAccessResponse.builder()
                                    .avatarUrl(presignedURL)
                                    .build();

        return ResponseEntity.ok()
            // .headers(generateCookie(fullCdnUrl))
            .body(new ApiResponse<>(true, "Avatar updated", cdnUrl));

    }        

    @PostMapping("/profile/bio")
    public ResponseEntity<ApiResponse<?>> updateBio(@RequestBody @Valid UpdateBioRequest bio) {
        String userId = (String) SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();
        
        userProfileService.updateBio(bio.getBio(), UUID.fromString(userId));
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Bio updated", null));
    }
    
}
