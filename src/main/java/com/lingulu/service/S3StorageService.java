package com.lingulu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Value("${aws.s3.bucket.chat.name}")
    private String chatBucketName;

    @Value("${aws.s3.bucket.profile.name}")
    private String profileBucketName;

    @Value("${aws.region}")
    private String region;

    public S3StorageService(S3Client s3Client, S3Presigner presigner) {
        this.s3Client = s3Client;
        this.presigner = presigner;
    }

    public String generatePresignedUrl(String s3Key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(chatBucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
                        .build();

        return presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }
    
    public void uploadMultipartFile(
            MultipartFile file,
            String s3Key,
            String bucketName
    ) throws IOException {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName.equals("profile") ? profileBucketName:chatBucketName)
                .key(s3Key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(file.getBytes())
        );
    }

    public void uploadBytes(
            byte[] data,
            String contentType,
            String s3Key
    ) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(chatBucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(data)
        );
    }

    public void deleteFile(String bucketName, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName.equals("profile") ? profileBucketName:chatBucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

}
