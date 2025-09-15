package com.Jacob.ridesafebackend.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.dto.PassengerStatusCoordiantesRequest;
import com.Jacob.ridesafebackend.models.LoginPassenger;
import com.Jacob.ridesafebackend.models.Passenger;
import com.Jacob.ridesafebackend.service.GoogleAuthentication;
import com.Jacob.ridesafebackend.service.PassengerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

//*FOR PASSENGER PROCESS ROUTES*
@CrossOrigin(origins = "http://localhost:3000")
@RestController // Changed from Controller to RestController
@RequestMapping("/api/v1/passengers")
@Tag(name = "Passengers", description = "Passenger management APIs")
public class PassengerController {
	// TODO need to make passenger service
	@Autowired
	private final PassengerService passengerServ;
	private final GoogleAuthentication GoogleAuth;
	

	public PassengerController(PassengerService passengerServ,GoogleAuthentication GoogleAuth) {
		this.passengerServ = passengerServ;
		this.GoogleAuth = GoogleAuth;
	}

	// ========================= CREATE PASSENGER =========================

	/**
	 * Creates a new passenger account and returns the saved object.
	 */
	@PostMapping("/api/v1/passengers/create")
	public ResponseEntity<Passenger> createPassenger(@RequestBody Passenger passenger, HttpSession session) {
		Passenger createPassenger = passengerServ.createPassenger(passenger);
		return ResponseEntity.ok(createPassenger);
	}


	// ========================= GET ALL PASSENGERS =========================

	/**
	 * Retrieves a list of all registered passengers.
	 */
	@GetMapping("/api/v1/passengers")
	public ResponseEntity<List<Passenger>> getAllPassenges() {
		List<Passenger> passenger = passengerServ.getAllPassengers();
		return ResponseEntity.ok(passenger);
	}


	// ========================= GET PASSENGER BY ID =========================

	/**
	 * Retrieves a single passenger by their ID.
	 */
	@GetMapping("/api/v1/passengers/{id}")
	public ResponseEntity<?> getPassengerById(@PathVariable("id") String id) {
		Optional<Passenger> passenger = passengerServ.getPassengerById(id);

		if (passenger.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger not found.");
		}

		return ResponseEntity.ok(passenger.get());
	}


	// ========================= PASSENGER LOGIN =========================

	/**
	 * Logs in a passenger using email and password.
	 */
	@PostMapping("/api/v1/auth/passenger/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody LoginPassenger loginPassenger, HttpSession session) {
		// Fetch passenger by email
		Passenger existingPassenger = passengerServ.getPassenger(loginPassenger.getEmail());

		if (existingPassenger == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Unknown email"));
		}

		// Validate password with BCrypt
		if (!BCrypt.checkpw(loginPassenger.getPassword(), existingPassenger.getPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Incorrect password"));
		}

		// Login successful
		return ResponseEntity.ok(Map.of("message", "Login successful", "id", existingPassenger.getId()));
	}


	// ========================= UPDATE PASSENGER COORDINATES =========================

	/**
	 * Updates a passenger's current coordinates (e.g. for nearby driver lookups).
	 */
	@PutMapping("/api/v1/passengers/{id}/status")
	public ResponseEntity<String> updatePassengerStatus(@PathVariable("id") String id, @RequestBody PassengerStatusCoordiantesRequest request) {
		passengerServ.updatePasengerStatus(id, request.getLongitude(), request.getLatitude());

		System.out.println("Passenger Coordinates" + request);

		return ResponseEntity.ok("Passenger Status updated");
	}
	
	// WORKING FOR MEG REPO
}
