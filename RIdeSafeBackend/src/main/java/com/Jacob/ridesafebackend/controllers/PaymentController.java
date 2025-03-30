package com.Jacob.ridesafebackend.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.RideRepository;
import com.Jacob.ridesafebackend.service.PaymentService;
import com.stripe.exception.StripeException;

@RestController
public class PaymentController {
	

    @Autowired
    private PaymentService paymentServ;

    @Autowired
    private RideRepository rideRepo;  // Inject RideRepository

    @PostMapping("/create-Payment-Intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, String> request) {
        try {
            String rideId = request.get("rideId");

            // Ensure rideId is provided
            if (rideId == null || rideId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ride ID is required"));
            }

            Optional<Ride> optionalRide = rideRepo.findById(rideId);
            
            if (optionalRide.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Ride not found"));
            }

            Ride ride = optionalRide.get();
            Map<String, String> clientSecretResponse = paymentServ.createPaymentIntent(ride);

            return ResponseEntity.ok(clientSecretResponse);
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Payment Intent Creation Failed"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }


}
