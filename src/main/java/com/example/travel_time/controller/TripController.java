package com.example.travel_time.controller;

import com.example.travel_time.model.Trip;
import com.example.travel_time.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        Optional<Trip> trip = tripService.getTripById(id);
        return trip.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Trip> addTrip(@RequestBody Trip trip) {
        Trip savedTrip = tripService.addTrip(trip);
        return ResponseEntity.ok(savedTrip);
    }

    @PutMapping("update_trip/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable Long id, @RequestBody Trip newTrip) {
        try {
            Trip updatedTrip = tripService.updateTrip(id, newTrip);
            return ResponseEntity.ok(updatedTrip);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete_trip/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }
}
