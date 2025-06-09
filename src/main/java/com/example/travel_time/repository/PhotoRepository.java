package com.example.travel_time.repository;

import com.example.travel_time.model.Photo;
import com.example.travel_time.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByUser(User user);
    boolean existsByIdAndUser(Long id, User user);
    void deleteByIdAndUser(Long id, User user);

    Optional<Photo> findByIdAndUser(Long id, User user);

    @Query("SELECT p FROM Photo p LEFT JOIN FETCH p.trip t LEFT JOIN FETCH t.user WHERE p.user.id = :userId")
    List<Photo> findPhotosByUserIdWithDetails(@Param("userId") Long userId);

    long countByUserId(Long userId);

    List<Photo> findByUserId(Long userId);
}