package com.example.travel_time.repository;

import com.example.travel_time.model.Photo;
import com.example.travel_time.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByUser(User user);
    boolean existsByIdAndUser(Long id, User user);
    void deleteByIdAndUser(Long id, User user);

    Optional<Photo> findByIdAndUser(Long id, User user);
}