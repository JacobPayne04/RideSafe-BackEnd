package com.Jacob.ridesafebackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.service.RideService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class RideController {

	@Autowired
	private final RideService rideServ; // Using rideServ for the variable name

    // Constructor for dependency injection
    public RideController(RideService rideServ) {
        this.rideServ = rideServ;
    }

    // Save a ride
    @PostMapping("/rides/save")
    public ResponseEntity<String> saveRide(@RequestBody Ride ride) {
        Ride savedRide = rideServ.saveRide(ride); // Using rideServ consistently
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Ride scheduled successfully with ID: " + savedRide.getId());
    }
	
	
}
