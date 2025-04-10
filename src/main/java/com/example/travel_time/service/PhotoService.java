package com.example.travel_time.service;

import com.example.travel_time.model.Photo;
import com.example.travel_time.model.User;
import com.example.travel_time.repository.PhotoRepository;
import com.example.travel_time.repository.UserRepository;
import com.example.travel_time.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final S3Service s3Service;
    private final UserRepository userRepository;

    public List<Photo> getUserPhotos(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return photoRepository.findAllByUser(user);
    }

    @Transactional
    public Photo uploadPhoto(MultipartFile file, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String fileUrl = s3Service.uploadFile(file);

        Photo photo = Photo.builder()
                .url(fileUrl)
                .fileName(file.getOriginalFilename())
                .user(user)
                .build();

        return photoRepository.save(photo);
    }

    @Transactional
    public void deletePhoto(Long photoId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Photo photo = photoRepository.findByIdAndUser(photoId, user)
                .orElseThrow(() -> new RuntimeException("Photo not found or not owned by user"));

        try {
            s3Service.deleteFile(photo.getUrl());
            photoRepository.delete(photo);
        } catch (RuntimeException e) {
            log.error("Failed to delete photo with ID: {}", photoId, e);
            throw new RuntimeException("Failed to delete photo", e);
        }
    }
}