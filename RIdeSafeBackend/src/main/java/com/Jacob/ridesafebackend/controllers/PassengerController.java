package com.Jacob.ridesafebackend.controllers;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.LoginPassenger;
import com.Jacob.ridesafebackend.models.Passenger;
import com.Jacob.ridesafebackend.service.GoogleAuthentication;
import com.Jacob.ridesafebackend.service.PassengerService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import jakarta.servlet.http.HttpSession;

//*FOR PASSENGER PROCESS ROUTES*
@CrossOrigin(origins = "http://localhost:3000")
@RestController // Changed from Controller to RestController
public class PassengerController {
	// TODO need to make passenger service
	@Autowired
	private final PassengerService passengerServ;
	private final GoogleAuthentication GoogleAuth;
	

	public PassengerController(PassengerService passengerServ,GoogleAuthentication GoogleAuth) {
		this.passengerServ = passengerServ;
		this.GoogleAuth = GoogleAuth;
	}

	@PostMapping("/new/passenger")
	public ResponseEntity<Passenger> createPassenger(@RequestBody Passenger passenger, HttpSession session) {

		Passenger createPassenger = passengerServ.createPassenger(passenger);

		return ResponseEntity.ok(createPassenger);
	}

	// Current Driver in session route
	@GetMapping("/passenger")
	public ResponseEntity<List<Passenger>> getAllPassenges() {
		List<Passenger> passenger = passengerServ.getAllPassengers();
		return ResponseEntity.ok(passenger);
	}

	// Getting One Driver
	@GetMapping("/passenger/{id}")
	public ResponseEntity<?> getPassengerById(@PathVariable("id") String id) { // Pass in the drivers Id to send to the
																				// frontend

		Optional<Passenger> passenger = passengerServ.getPassengerById(id); // fetching a driver by its id

		if (passenger.isEmpty()) { // if the driver does not exist, return "Driver not found."
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Passenger not found.");
		}

		return ResponseEntity.ok(passenger.get()); // return the Driver
	}

	@PostMapping("/login/passenger")
	    public ResponseEntity<Map<String, String>> login(@RequestBody LoginPassenger loginPassenger,HttpSession session) {
	        // Fetch the driver by email
		   Passenger existingPassenger = passengerServ.getPassenger(loginPassenger.getEmail());
	        
	        if (existingPassenger == null) {
	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of("message", "Unknown email"));
	        }

	        // Check the password using BCrypt
	        if (!BCrypt.checkpw(loginPassenger.getPassword(), existingPassenger.getPassword())) {
	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of("message", "Incorrect password"));
	        }
	   
	        // Login successful
	        return ResponseEntity.ok(Map.of("message", "Login successful","id",existingPassenger.getId()));
	      
	    }


//	@PostMapping("/signup/{role}/googleId")
//	public ResponseEntity<?> googleSignIn(@PathVariable String role, @RequestBody Map<String, String> requestBody, HttpSession session) {
//	    try {
//	        String idToken = requestBody.get("googleId");
//	        System.out.println("Received Google ID Token: " + idToken);
//	        System.out.println("Received Role: " + role);
//
//	        // Validate the Google ID token
//	        if (idToken == null || idToken.isEmpty()) {
//	            System.out.println("Error: Missing or invalid Google ID token");
//	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Google ID token");
//	        }
//
//	        // Verify the Google ID token
//	        GoogleIdToken.Payload payload = GoogleAuthentication.verifyGoogleToken(idToken);
//	        System.out.println("Google Token Verification Payload: " + payload);
//
//	        if (payload == null) {
//	            System.out.println("Error: Invalid Google Token");
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google Token");
//	        }
//
//	        // Extract email and Google ID from the payload
//	        String email = payload.getEmail();
//	        String googleId = payload.getSubject();
//	        System.out.println("Extracted Email: " + email);
//	        System.out.println("Extracted Google ID: " + googleId);
//
//	        // Check if the role is 'driver'
//	        if ("passenger".equals(role)) {
//	            Optional<Passenger> existingPassenger = passengerServ.getPassengerByEmail(email);
//	            System.out.println("Passenger Lookup Result: " + existingPassenger);
//
//	            if (existingPassenger.isPresent()) {
//	                Passenger passenger = existingPassenger.get();
//	                session.setAttribute("passengerId", passenger.getId());
//	                System.out.println("Passenger Exists. ID: " + passenger.getId());
//
//	                return ResponseEntity.ok(Map.of(
//	                    "exists", true,
//	                    "passengerId", passenger.getId(),
//	                    "message", "Passenger found, proceed to home."
//	                ));
//	            } else {
//	                System.out.println("New Passenger Detected. Redirecting to Registration.");
//
//	                return ResponseEntity.ok(Map.of(
//	                    "exists", false,
//	                    "message", "New Passenger, please proceed to registration."
//	                ));
//	            }
//	        }
//
//	        System.out.println("Error: Invalid role received for Passenger - " + role);
//	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role: " + role);
//
//	    } catch (IOException e) {
//	        System.out.println("Google Authentication Failed for Passenger: " + e.getMessage());
//	        e.printStackTrace();  // Log full error details
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google authentication failed for Passenger: " + e.getMessage());
//	    } catch (Exception e) {
//	        System.out.println("Unexpected Error: " + e.getMessage());
//	        e.printStackTrace();  // Log full error details
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google Sign-In Failed for Passenger: " + e.getMessage());
//	    }
//	}
	
	
	
	
	
}
