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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Jacob.ridesafebackend.dto.DriverRequiredInformationDTO;
import com.Jacob.ridesafebackend.dto.DriverStatusCoordinatesRequest;
import com.Jacob.ridesafebackend.dto.PassengerStatusCoordiantesRequest;
import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.LoginDriver;
import com.Jacob.ridesafebackend.models.Passenger;
import com.Jacob.ridesafebackend.service.DriverService;
import com.Jacob.ridesafebackend.service.GoogleAuthentication;
import com.Jacob.ridesafebackend.service.PassengerService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController // Changed from Controller to RestController
public class DriverController {

	@Autowired
	private final DriverService driverServ;
	private final GoogleAuthentication GoogleAuth;
	private final PassengerService passengerServ;

	public DriverController(DriverService driverServ, GoogleAuthentication GoogleAuth, PassengerService passengerServ) {
		this.driverServ = driverServ;
		this.GoogleAuth = GoogleAuth;
		this.passengerServ = passengerServ;
	}

	@PostMapping("/new")
	public ResponseEntity<Driver> createDriver(@RequestBody Driver driver, HttpSession session) {

		Driver creatDriver = driverServ.creatDriver(driver);

		return ResponseEntity.ok(creatDriver);
	}
	
	//Start of implimenting new method 
	@PostMapping("/Driver/complete/signup")
	public ResponseEntity<?> SubmitDriverApplication( @RequestPart("info") DriverRequiredInformationDTO info,
			@RequestPart("dlFile") MultipartFile dlFile,
		    @RequestPart("studentIdFile") MultipartFile studentIdFile){
		
		return null;
		
	}
	
	
	

	// Current Driver in session route

	// Getting One Driver
	@GetMapping("/driver/{id}")
	public ResponseEntity<?> getDriverById(@PathVariable("id") String id) { // Pass in the drivers Id to send to the
																			// frontend

		Optional<Driver> driver = driverServ.getDriverById(id); // fetching a driver by its id

		if (driver.isEmpty()) { // if the driver does not exist, return "Driver not found."
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found.");
		}

		return ResponseEntity.ok(driver.get()); // return the Driver
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody LoginDriver loginDriver, HttpSession session) {
		// Fetch the driver by email
		Driver existingDriver = driverServ.getDriver(loginDriver.getEmail());

		if (existingDriver == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Unknown email"));
		}

		// Check the password using BCrypt
		if (!BCrypt.checkpw(loginDriver.getPassword(), existingDriver.getPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Incorrect password"));
		}

		// Login successful
		return ResponseEntity.ok(Map.of("message", "Login successful", "id", existingDriver.getId()));
	}

	// ROUTES FOR DRIVER
	// FUNCTIONALITY*******************************************************************************************
		
	//*************ended here*********4/24/25
	@PutMapping("/{id}/status")
	public ResponseEntity<String> updateDriverStatus(@PathVariable("id") String id,
			@RequestBody DriverStatusCoordinatesRequest request) {
		driverServ.updateStatus(id, request.isOnline(), request.getLongitude(), request.getLatitude());
		System.out.println("Driver cordinates" + request);
		System.out.println("Received request: driver isOnline=" + request.isOnline() + ", lat=" + request.getLatitude());

		return ResponseEntity.ok("Drive Status updated");
	}

	@GetMapping("/online/drivers")
	public ResponseEntity<List<Driver>> GetIsOnlineDrivers() {

		List<Driver> onlineDrivers = driverServ.getIsOnlineDrivers();
		return ResponseEntity.ok(onlineDrivers);

	}
	
	//NEW METHOD for nearby drivers
	@PostMapping("/nearby/drivers")
	public ResponseEntity<List<Driver>> getNearbyDrivers(@RequestBody PassengerStatusCoordiantesRequest passengerStatusCoordinatesRequest) {
	    double latitude = passengerStatusCoordinatesRequest.getLatitude();
	    double longitude = passengerStatusCoordinatesRequest.getLongitude();

	    List<Driver> nearbyDrivers = driverServ.findNearbyDrivers(latitude, longitude);
	    return ResponseEntity.ok(nearbyDrivers);
	}

	@PutMapping("/edit/driver/{id}")
	public ResponseEntity<Driver> updateDriver(@PathVariable String id, @RequestBody Driver updatedDriver) {
		Driver driver = driverServ.updateDriver(id, updatedDriver);
		return ResponseEntity.ok(driver);
	}

	@PostMapping("/signup/{role}/googleId")
	public ResponseEntity<?> googleSignIn(@PathVariable String role, @RequestBody Map<String, String> requestBody,
			HttpSession session) {
		try {
			String idToken = requestBody.get("googleId");
			System.out.println("Received Google ID Token: " + idToken);
			System.out.println("Received Role: " + role);

			// Validate the Google ID token
			if (idToken == null || idToken.isEmpty()) {
				System.out.println("Error: Missing or invalid Google ID token");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Google ID token");
			}

			// Verify the Google ID token
			GoogleIdToken.Payload payload = GoogleAuthentication.verifyGoogleToken(idToken);
			System.out.println("Google Token Verification Payload: " + payload);

			if (payload == null) {
				System.out.println("Error: Invalid Google Token");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google Token");
			}

			// Extract email and Google ID from the payload
			String email = payload.getEmail();
			String googleId = payload.getSubject();
			System.out.println("Extracted Email: " + email);
			System.out.println("Extracted Google ID: " + googleId);

			// Check if the role is 'driver'
			if ("driver".equals(role)) {
				Optional<Driver> existingDriver = driverServ.getDriverByEmail(email);
				System.out.println("Driver Lookup Result: " + existingDriver);

				if (existingDriver.isPresent()) {
					Driver driver = existingDriver.get();
					session.setAttribute("driverId", driver.getId());
					System.out.println("Driver Exists. ID: " + driver.getId());

					return ResponseEntity.ok(Map.of("exists", true, "driverId", driver.getId(), "message",
							"Driver found, proceed to home."));
				} else {
					System.out.println("New Driver Detected. Redirecting to Registration.");

					return ResponseEntity
							.ok(Map.of("exists", false, "message", "New driver, please proceed to registration."));
				}
			}

			if ("passenger".equals(role)) {
				Optional<Passenger> existingPassenger = passengerServ.getPassengerByEmail(email);
				System.out.println("Passenger Lookup Result: " + existingPassenger);

				if (existingPassenger.isPresent()) {
					Passenger passenger = existingPassenger.get();
					session.setAttribute("passengerId", passenger.getId());
					System.out.println("Passenger Exists. ID: " + passenger.getId());

					return ResponseEntity.ok(Map.of("exists", true, "passengerId", passenger.getId(), "message",
							"Passenger found, proceed to home."));
				} else {
					System.out.println("New Passenger Detected. Redirecting to Registration.");

					return ResponseEntity
							.ok(Map.of("exists", false, "message", "New Passenger, please proceed to registration."));
				}
			}

			System.out.println("Error: Invalid role received - " + role);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role: " + role);

		} catch (IOException e) {
			System.out.println("Google Authentication Failed: " + e.getMessage());
			e.printStackTrace(); // Log full error details
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Google authentication failed: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error: " + e.getMessage());
			e.printStackTrace(); // Log full error details
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Google Sign-In Failed: " + e.getMessage());
		}
	}
	

}
