package com.example.travel_time.service;

import com.example.travel_time.model.Photo;
import com.example.travel_time.model.Trip;
import com.example.travel_time.model.User;
import com.example.travel_time.repository.PhotoRepository;
import com.example.travel_time.repository.TripRepository;
import com.example.travel_time.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return authentication.getName();
    }

    private User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<Photo> getAuthenticatedUserPhotos() {
        User user = getCurrentUser();
        return photoRepository.findAllByUser(user);
    }

    @Transactional
    public List<Photo> uploadPhotos(MultipartFile[] files, Long tripId) throws IOException {
        User user = getCurrentUser();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));

        List<Photo> uploadedPhotos = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileUrl = s3Service.uploadFile(file);

            Photo photo = Photo.builder()
                    .url(fileUrl)
                    .fileName(file.getOriginalFilename())
                    .user(user)
                    .trip(trip)
                    .uploadDate(LocalDateTime.now())
                    .build();

            uploadedPhotos.add(photoRepository.save(photo));
        }

        return uploadedPhotos;
    }

    @Transactional
    public void deletePhoto(Long photoId) {
        User user = getCurrentUser();

        Photo photo = photoRepository.findByIdAndUser(photoId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found or not owned by user"));

        try {
            s3Service.deleteFile(photo.getUrl());
            photoRepository.delete(photo);
        } catch (RuntimeException e) {
            log.error("Failed to delete photo with ID: {}", photoId, e);
            throw new RuntimeException("Failed to delete photo", e);
        }
    }

    public List<Photo> getUserPhotos(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return photoRepository.findPhotosByUserIdWithDetails(user.getId());
    }
}
