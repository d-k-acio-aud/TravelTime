package com.example.travel_time.service;

import com.example.travel_time.model.Trip;
import com.example.travel_time.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findById(id);
    }

    public Trip addTrip(Trip trip) {
        return tripRepository.save(trip);
    }



    public Trip updateTrip(Long id, Trip newTrip) {
        return tripRepository.findById(id)
                .map(trip -> {
                    trip.setName(newTrip.getName());
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
}
