package com.example.travel_time.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    @Autowired
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
        Dotenv dotenv = Dotenv.configure().filename("amazon.env").load();
        this.bucketName = dotenv.get("AWS_S3_BUCKET");
        this.region = dotenv.get("AWS_REGION");
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String generatedFileName = UUID.randomUUID() + fileExtension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(generatedFileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    //.acl("public-read")
                    .build();


            s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(file.getBytes()));

            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, generatedFileName);

            log.info("File uploaded successfully: {}", fileUrl);
            return fileUrl;

        } catch (S3Exception e) {
            log.error("S3 upload error: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileKey = extractFileKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}", fileKey);

        } catch (S3Exception e) {
            log.error("S3 delete error: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    private String extractFileKeyFromUrl(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            String path = uri.getPath();
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (URISyntaxException e) {
            log.error("Invalid S3 file URL: {}", fileUrl);
            throw new IllegalArgumentException("Invalid S3 file URL", e);
        }
    }
}