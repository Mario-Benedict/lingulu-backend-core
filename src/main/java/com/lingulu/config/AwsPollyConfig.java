package com.lingulu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;

@Configuration
public class AwsPollyConfig {

    @Value("{aws.endpoint}")
    private String endpoint;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("{aws.access.key}")
    private String accessKey;

    @Value("{aws.secret.key}")
    private String secretKey;

    @Bean
    public PollyClient pollyClient() {
        return PollyClient.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }
}
