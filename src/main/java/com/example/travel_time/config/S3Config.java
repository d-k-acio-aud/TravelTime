package com.example.travel_time.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    private final Dotenv dotenv = Dotenv.configure().filename("amazon.env").load();


    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(dotenv.get("AWS_REGION")))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        dotenv.get("AWS_ACCESS_KEY"),
                        dotenv.get("AWS_SECRET_KEY")
                )))
                .build();
    }
}
