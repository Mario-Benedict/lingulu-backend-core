package com.lingulu.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingulu.dto.response.general.ApiResponse;
import com.lingulu.dto.response.info.LeaderboardResponse;
import com.lingulu.dto.response.info.UserRankResponse;
import com.lingulu.service.CloudFrontSigner;
import com.lingulu.service.LeaderboardService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCustomPolicy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpHeaders;



@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderBoardController {
    
    private final LeaderboardService leaderboardService;
    private final CloudFrontSigner cloudFrontSigner;

    private HttpHeaders generateCookie(String cdnUrl){
        CookiesForCustomPolicy signedCookies = cloudFrontSigner.generateSignedCookiesMultiUrl(cdnUrl);

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

        ResponseCookie expiresCookie = ResponseCookie.from("CloudFront-Policy", signedCookies.policyHeaderValue().split("=")[1])
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

    // Need /all to avoid conflict with /user-rank. If not, will cause internal server eror
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<LeaderboardResponse>>> leaderboard() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();


        List<LeaderboardResponse> leaderboardResponse = leaderboardService.getTop10Leaderboards();

        leaderboardResponse.forEach(lr ->
                lr.setAvatarUrl(cloudFrontSigner.generateCdnUrl(lr.getAvatarUrl()))
        );

        return ResponseEntity.ok()
            .headers(generateCookie(cloudFrontSigner.generateCdnUrlWithWildcard("users/avatars/*")))
            .body(new ApiResponse<>(true, "Top 10 Leaderboard received successfully", leaderboardResponse));
    }

    @GetMapping("/user-rank")
    public ResponseEntity<ApiResponse<?>> userRank() {
        String userId = (String)SecurityContextHolder.getContext()
                       .getAuthentication().getPrincipal();

        UserRankResponse response = leaderboardService.getUserRank(UUID.fromString(userId));
        String fullCdnUrl = cloudFrontSigner.generateCdnUrl(response.getAvatarUrl());
        response.setAvatarUrl(fullCdnUrl);
        
        return ResponseEntity.ok()
            .headers(generateCookie(fullCdnUrl))
            .body(new ApiResponse<>(true, "User Rank received", response));
    }
    
    
}
