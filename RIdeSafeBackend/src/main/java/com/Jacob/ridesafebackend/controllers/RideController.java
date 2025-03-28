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
import com.Jacob.ridesafebackend.service.RideService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class RideController {

	private final SimpMessagingTemplate messagingTemplate;

	@Autowired
	private final RideService rideServ; // Using rideServ for the variable name

	// Constructor for dependency injection
	public RideController(RideService rideServ, SimpMessagingTemplate messagingTemplate) {
		this.rideServ = rideServ;
		this.messagingTemplate = messagingTemplate;
	}

	@PostMapping("/rides/save")
	public ResponseEntity<String> saveRide(@RequestBody Ride ride) {
		Ride savedRide = rideServ.saveRide(ride); // Using rideServ consistently
		System.out.println("Notification sent to /topic/driver/" + savedRide.getDriverId());

		Map<String, Object> notification = new HashMap<>();
		notification.put("message", "New ride request from passenger.");
		notification.put("passengerId", savedRide.getPassengerId());
		notification.put("rideId", savedRide.getId());
		notification.put("status", savedRide.getStatus());
		notification.put("fromLocation", savedRide.getFromLocation());
		notification.put("fromLatitude", savedRide.getFromLatitude());
		notification.put("fromLongitude", savedRide.getFromLongitude());
		notification.put("toLocation", savedRide.getToLocation());
		notification.put("toLatitude", savedRide.getToLatitude());
		notification.put("toLongitude", savedRide.getToLongitude());

		messagingTemplate.convertAndSend("/topic/driver/" + savedRide.getDriverId(), notification);
		//##TODO this we need await conformation to call upon the payment trigger for the backend *****current thoughts is i dont know how exactly yet to connet teh webesock trigger to teh payment frono end so no we need to add a wawait acll back adn upon the success it triggers a fornt end to route to the pay page and then the pag pag will render teh othetr 
		

		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Ride scheduled successfully with ID: " + savedRide.getId());
	}

	// Get ride by Id
	@GetMapping("/ride/{id}")
	public ResponseEntity<Ride> getRideById(@PathVariable String id) {
		Optional<Ride> ride = rideServ.getRideById(id);
		return ride.map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	

	@PutMapping("/{id}/accept/complete") // TODO# add ongoing in route, and add IN_QUEUE in enum in ride model TODO#
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

	@PutMapping("/{rideId}/accept/{driverId}")
	public ResponseEntity<Ride> acceptRide(@PathVariable String rideId, @PathVariable String driverId) {
		Ride ride = rideServ.acceptRide(rideId, driverId);
		if (ride != null) {
			return new ResponseEntity<>(ride, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@PutMapping("/{rideId}/complete")
	public ResponseEntity<Ride> completeRide(@PathVariable String rideId, @RequestParam String driverId) {
		Ride ride = rideServ.completeRide(rideId,driverId);
		if (ride != null) {
			return new ResponseEntity<>(ride, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// Get ongoing rides for driver by driver ID
	@GetMapping("/driver/{id}/rides/ongoing")
	public ResponseEntity<List<Ride>> getOngoingRidesByDriverId(@PathVariable String id) {
		List<Ride> rides = rideServ.getOngoingRidesByDriverId(id);

		if (rides.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.ok(rides);
	}

	@GetMapping("/{id}/MapRoute")
	public ResponseEntity<Map<String, String>> getRideMapUrl(@PathVariable String id) {
		String googleMapsUrl = rideServ.getGoogleMapsUrl(id);
		return ResponseEntity.ok(Map.of("googleMapsUrl", googleMapsUrl));
	}

}