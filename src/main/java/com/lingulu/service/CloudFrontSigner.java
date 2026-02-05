package com.lingulu.service;

import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;


import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;


@Service
public class CloudFrontSigner {
    
    private final String keyPairId;
    private final String cdnDomain;
    private final PrivateKey privateKey;

    private PrivateKey loadPrivateKey(String privateKeyPath) {
        try {
            byte[] keyBytes = Files.readAllBytes(Path.of(privateKeyPath));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Gagal memuat Private Key dari file .der: " + privateKeyPath + 
                    ". Pastikan file tersebut adalah format PKCS#8 biner.", e
            );
        }
    }

    public CloudFrontSigner(
            @Value("${aws.cloudfront.key-pair-id}") String keyPairId,
            @Value("${cdn.domain}") String cdnDomain,
            @Value("${aws.cloudfront.private-key.path}") String privateKeyPath
    ) {
        this.keyPairId = keyPairId;
        this.cdnDomain = cdnDomain;
        this.privateKey = loadPrivateKey(privateKeyPath);
    }

    /** Generate URL CDN **/
    public String generateCdnUrl(String s3Key) {
        return "https://" + cdnDomain + "/" + s3Key;
    }

    /** Generate CloudFront Signed Cookies (valid 1 hari) */
    public CookiesForCannedPolicy generateSignedCookies(String resourceUrl) {

        Instant expiration = Instant.now()
                .plus(7, ChronoUnit.DAYS);

        CloudFrontUtilities utilities = CloudFrontUtilities.create();

        CannedSignerRequest request = CannedSignerRequest.builder()
                .resourceUrl(resourceUrl)
                .keyPairId(keyPairId)
                .privateKey(privateKey)
                .expirationDate(expiration)
                .build();

        return utilities.getCookiesForCannedPolicy(request);
    }

}
