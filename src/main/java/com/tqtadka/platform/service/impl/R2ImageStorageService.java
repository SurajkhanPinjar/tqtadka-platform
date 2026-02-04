package com.tqtadka.platform.service.impl;

import com.tqtadka.platform.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "cdn.mode", havingValue = "r2")
public class R2ImageStorageService implements ImageStorageService {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    public R2ImageStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String upload(MultipartFile file) {

        try {
            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );

            // ✅ Always CDN URL
            return publicUrl + "/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("R2 upload failed", e);
        }
    }

    @Override
    public void delete(String imageUrl) {
        // optional
    }
}