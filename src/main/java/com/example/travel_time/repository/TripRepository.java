package com.example.travel_time.repository;


import com.example.travel_time.model.Trip;
import com.example.travel_time.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserId(Long userId);


    List<Trip> findByUserUsername(String username);


    long countByUserUsername(String username);


    List<Trip> findByUserEmail(String email);


    long countByUserEmail(String email);

    int countByUserId(Long userId);

    List<Trip> findByUserUsernameOrderByStartDateDesc(String username);

}

