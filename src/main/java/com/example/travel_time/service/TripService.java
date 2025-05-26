package com.example.travel_time.service;

import com.example.travel_time.model.Photo;
import com.example.travel_time.model.Trip;
import com.example.travel_time.model.User;
import com.example.travel_time.repository.TripRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserService userService;

    public TripService(TripRepository tripRepository, UserService userService) {
        this.tripRepository = tripRepository;
        this.userService = userService;
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findById(id);
    }

    public List<Trip> getUserTrips(String username) {
        return tripRepository.findByUserUsername(username);
    }

    public long getUserTripCount(String username) {
        return tripRepository.countByUserUsername(username);
    }

    public Trip addTrip(Trip trip, String username) {
        if (trip.getDescription() == null || trip.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
        }
        User user = userService.findByUsername(username);
        trip.setUser(user);
        return tripRepository.save(trip);
    }

    public Trip updateTrip(Long id, Trip newTrip) {
        return tripRepository.findById(id)
                .map(trip -> {
                    trip.setName(newTrip.getName());
                    trip.setDescription(newTrip.getDescription());
                    trip.setDestination(newTrip.getDestination());
                    trip.setStartDate(newTrip.getStartDate());
                    trip.setEndDate(newTrip.getEndDate());
                    return tripRepository.save(trip);
                })
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));
    }

    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }

    public List<Photo> getTripPhotos(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"))
                .getPhotos();
    }
}
