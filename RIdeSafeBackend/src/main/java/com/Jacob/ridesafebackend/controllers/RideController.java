package com.Jacob.ridesafebackend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.RideRepository;
import com.Jacob.ridesafebackend.service.PaymentService;
import com.Jacob.ridesafebackend.service.RideService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class RideController {

	private final SimpMessagingTemplate messagingTemplate;

	@Autowired
	private final RideService rideServ; // Using rideServ for the variable name
	private final RideRepository rideRepo;
	private final PaymentService paymentServ;

	// Constructor for dependency injection
	public RideController(RideService rideServ, SimpMessagingTemplate messagingTemplate, RideRepository rideRepo, PaymentService paymentServ) {
		this.rideServ = rideServ;
		this.messagingTemplate = messagingTemplate;
		this.rideRepo = rideRepo;
		this.paymentServ = paymentServ;
	}

	@PostMapping("/api/v1/rides/save")
	public ResponseEntity<Map<String, Object>> saveRide(@RequestBody Ride ride) {
	    Ride savedRide = rideServ.saveRide(ride);

	    Map<String, Object> response = new HashMap<>();
	    response.put("message", "Ride scheduled successfully");
	    response.put("rideId", savedRide.getId());
	    response.put("passengerAmount", savedRide.getPassengerAmount());

	    // Notify the driver via WebSocket
	    

	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}



	// Get ride by Id
	@GetMapping("/api/v1/rides/{id}")
	public ResponseEntity<Ride> getRideById(@PathVariable String id) {
		Optional<Ride> ride = rideServ.getRideById(id);
		return ride.map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	

	@PutMapping("/api/v1/rides/{id}/accept/complete") // TODO# add ongoing in route, and add IN_QUEUE in enum in ride model TODO#
											// make
	// "acceptRide" service method to acceptRideOngoing
	public ResponseEntity<String> acceptRideComplete(@PathVariable String id) {
		try {
			// Convert the String "COMPLETED" to RideStatus enum
			Ride.RideStatus status = Ride.RideStatus.valueOf("COMPLETED");

			// Call the service with the converted enum
			rideServ.updateRideStatus(id, status);

			return ResponseEntity.ok("Ride accepted and updated to Ongoing.");
		} catch (IllegalArgumentException e) {
			// This will catch invalid values
			return ResponseEntity.badRequest().body("Invalid status value.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride not found.");
		}
	}

	@PutMapping(" /api/v1/rides/{rideId}/accept/{driverId}")
	public ResponseEntity<Ride> acceptRide(@PathVariable String rideId, @PathVariable String driverId) {
		Ride ride = rideServ.acceptRide(rideId, driverId);
		if (ride != null) {
			return new ResponseEntity<>(ride, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@PutMapping("/api/v1/rides/{rideId}/complete")
	public ResponseEntity<Ride> completeRide(@PathVariable String rideId, @RequestParam String driverId) {
		Ride ride = rideServ.completeRide(rideId,driverId);
		if (ride != null) {
			rideServ.sendPassengerRatingPrompt(rideId);
			return new ResponseEntity<>(ride, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// Get ongoing rides for driver by driver ID
	@GetMapping("/api/v1/drivers/{id}/rides/ongoing")
	public ResponseEntity<List<Ride>> getOngoingRidesByDriverId(@PathVariable String id) {
		List<Ride> rides = rideServ.getOngoingRidesByDriverId(id);

		if (rides.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.ok(rides);
	}
	

	//needs to change api route to make it better to fit in webcongif#TODO
	@GetMapping("/api/v1/rides/{id}/MapRoute")
	public ResponseEntity<Map<String, String>> getRideMapUrl(@PathVariable String id) {
		String googleMapsUrl = rideServ.getGoogleMapsUrl(id);
		return ResponseEntity.ok(Map.of("googleMapsUrl", googleMapsUrl));
	}
	
	@PostMapping("/api/v1/rides/update-ride-payment")
	public ResponseEntity<?> updateRidePayment(@RequestBody Map<String, String> payload) {
	    String rideId = payload.get("rideId");

	    if (rideId == null || rideId.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body(Map.of("error", "Ride ID is required."));
	    }

	    Optional<Ride> updatedRide = paymentServ.updateRidePaymentAmount(rideId);

	    if (updatedRide.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Ride not found or already paid."));
	    }

	    Ride ride = updatedRide.get();

	    // Include driverId in the response if available
	    Map<String, Object> response = new HashMap<>();
	    response.put("message", "Ride payment updated successfully.");
	    response.put("rideId", ride.getId());
	    response.put("driverId", ride.getDriverId());

	    return ResponseEntity.ok(response);
	}


	
	 @GetMapping("/api/v1/rides/details/{rideId}")
    public ResponseEntity<Ride> getRideDetails(@PathVariable String rideId) {
        Optional<Ride> rideOptional = rideServ.getRideById(rideId);
        if (rideOptional.isPresent()) {
            return ResponseEntity.ok(rideOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if ride not found
    }


}