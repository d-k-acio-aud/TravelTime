package com.example.travel_time.repository;

import com.example.travel_time.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserId(Long userId);

}
