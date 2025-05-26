package com.example.travel_time.controller;

import com.example.travel_time.model.Photo;
import com.example.travel_time.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping
    public ResponseEntity<List<Photo>> getUserPhotos() {
        return ResponseEntity.ok(photoService.getAuthenticatedUserPhotos());
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Photo>> uploadPhotos(
            @RequestParam("photos") MultipartFile[] files,
            @RequestParam Long tripId) throws IOException {
        return ResponseEntity.ok(photoService.uploadPhotos(files, tripId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id) {
        try {
            photoService.deletePhoto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting photo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete photo"));
        }
    }
}
