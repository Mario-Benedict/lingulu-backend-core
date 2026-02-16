package com.lingulu.service;

import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCustomPolicy;


@Service
public class CloudFrontSigner {
    
    private final String keyPairId;
    private final String cdnDomain;
    private final PrivateKey privateKey;

    private PrivateKey loadPrivateKey(String privateKeyPath) {
        try {
            ClassPathResource resource = new ClassPathResource(privateKeyPath);

            byte[] keyBytes = resource.getInputStream().readAllBytes();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load Private Key from file .der: " + privateKeyPath +
                    ". Please make sure the file is in PKCS#8 binary format.", e
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

    public String generateCdnUrl(String s3Key) {
        return "https://" + cdnDomain + "/" + s3Key;
    }

    public String generateSignedUrl(String s3Key) {

        Instant expiration = Instant.now().plus(7, ChronoUnit.DAYS);
        String resourceUrl = generateCdnUrl(s3Key);

        CloudFrontUtilities utilities = CloudFrontUtilities.create();

        CannedSignerRequest request = CannedSignerRequest.builder()
                .resourceUrl(resourceUrl)
                .keyPairId(keyPairId)
                .privateKey(privateKey)
                .expirationDate(expiration)
                .build();

        SignedUrl signedUrl = utilities.getSignedUrlWithCannedPolicy(request);

        return signedUrl.url();
    }
}
