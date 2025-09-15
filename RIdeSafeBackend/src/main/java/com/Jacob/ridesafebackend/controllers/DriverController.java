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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Jacob.ridesafebackend.dto.DriverRequiredInformationDTO;
import com.Jacob.ridesafebackend.dto.DriverStatusCoordinatesRequest;
import com.Jacob.ridesafebackend.dto.PassengerStatusCoordiantesRequest;
import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.DriverRating;
import com.Jacob.ridesafebackend.models.LoginDriver;
import com.Jacob.ridesafebackend.models.Passenger;
import com.Jacob.ridesafebackend.repositorys.DriverRatingRepository;
import com.Jacob.ridesafebackend.service.DriverService;
import com.Jacob.ridesafebackend.service.GoogleAuthentication;
import com.Jacob.ridesafebackend.service.PassengerService;
import com.Jacob.ridesafebackend.service.PaymentService;
import com.Jacob.ridesafebackend.service.jwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController // Changed from Controller to RestController
@RequestMapping("/api/v1/drivers")
@Tag(name = "Drivers", description = "Driver management APIs")
public class DriverController {

	@Autowired
	private final DriverService driverServ;
	private final GoogleAuthentication GoogleAuth;
	private final PassengerService passengerServ;
	private final PaymentService paymentServ;
	private final DriverRatingRepository driverRatingRepo;
	private final jwtService jwtService;

	public DriverController(DriverService driverServ, GoogleAuthentication GoogleAuth, PassengerService passengerServ,PaymentService paymentServ, DriverRatingRepository driverRatingRepo,jwtService jwtService) {
		this.driverServ = driverServ;
		this.GoogleAuth = GoogleAuth;
		this.passengerServ = passengerServ;
		this.paymentServ = paymentServ;
		this.driverRatingRepo = driverRatingRepo;
		this.jwtService = jwtService;
	}

	// ========================= CREATE DRIVER =========================

	  @Operation(summary = "Create a new driver",
              description = "Creates a new driver in the system and returns the created driver object.")
	@PostMapping("/api/v1/drivers/create")
	public ResponseEntity<Driver> createDriver(@RequestBody Driver driver, HttpSession session) {
		Driver creatDriver = driverServ.creatDriver(driver);
		return ResponseEntity.ok(creatDriver);
	}


	// ========================= SUBMIT DRIVER APPLICATION =========================

	   @Operation(summary = "Submit driver application",
               description = "Uploads required information and document files to complete driver signup.")
	@PostMapping("/api/v1/drivers/signup/complete")
	public ResponseEntity<?> submitDriverApplication(
	        @RequestPart("info") DriverRequiredInformationDTO info,
	        @RequestPart("dlFile") MultipartFile dlFile,
	        @RequestPart("studentIdFile") MultipartFile studentIdFile) {

	    try {
	        driverServ.processDriverRequiredInformationSignup(info, dlFile, studentIdFile);
	        return ResponseEntity.ok("Driver application submitted successfully.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Signup failed: " + e.getMessage());
	    }
	}
	
	// ========================= DECLINE DRIVER =============================
	
	   @Operation(summary = "Decline driver by ID",
			   description = "Allows admin to decline a driver application by their ID.")
	@PutMapping("/api/v1/admin/drivers/{id}/decline")
	public ResponseEntity<String> declineDriver(@PathVariable String id) {
	    boolean success = driverServ.declineDriver(id);
	    if (success) {
	        return ResponseEntity.ok("Driver declined successfully.");
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found.");
	    }
	}



	// ========================= GET DRIVER BY ID =========================

	   @Operation(summary = "Get driver by ID",
               description = "Retrieves a driver object using their unique ID.")
	@GetMapping("/api/v1/driver/{id}")
	public ResponseEntity<?> getDriverById(@PathVariable("id") String id) {
		Optional<Driver> driver = driverServ.getDriverById(id);

		if (driver.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found.");
		}

		return ResponseEntity.ok(driver.get());
	}
	
	// ========================= DRIVER RATING =======================
	
	   @Operation(summary = "Rate a driver",
               description = "Allows a passenger to submit a review rating for a driver.")
	@PutMapping("/api/v1/driver/send/Review")
	public ResponseEntity<?> rateDriver(@RequestBody Map<String, String> payload) {
	    String driverId = payload.get("driverId");
	    String passengerId = payload.get("passengerId");
	    int stars = Integer.parseInt(payload.get("stars"));

	    Optional<DriverRating> existingRating = driverRatingRepo.findByPassengerIdAndDriverId(passengerId, driverId);
	    if (existingRating.isPresent()) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("You already rated this driver.");
	    }

	    Optional<Driver> optionalDriver = driverServ.getDriverById(driverId);
	    if (!optionalDriver.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found.");
	    }

	    Driver driver = optionalDriver.get();

	    // Save the new rating
	    DriverRating rating = new DriverRating();
	    rating.setPassengerId(passengerId);
	    rating.setDriverId(driverId);
	    rating.setStars(stars);
	    driverRatingRepo.save(rating);

	    // Update the driver's rating stats
	    int newRatingSum = driver.getRatingSum() + stars;
	    int newTotal = driver.getTotalRatings() + 1;
	    double newAverage = (double) newRatingSum / newTotal;

	    driver.setRatingSum(newRatingSum);
	    driver.setTotalRatings(newTotal);
	    driver.setAverageRating(newAverage);

	    driverServ.save(driver);

	    return ResponseEntity.ok("Rating submitted successfully.");
	}


	// ========================= DRIVER LOGIN =========================

	    @Operation(summary = "Driver login",
	               description = "Logs in a driver using email and password, verifies credentials with BCrypt.")
	@PostMapping("/api/v1/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody LoginDriver loginDriver, HttpSession session) {
		Driver existingDriver = driverServ.getDriver(loginDriver.getEmail());

		if (existingDriver == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Unknown email"));
		}

		if (!BCrypt.checkpw(loginDriver.getPassword(), existingDriver.getPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Incorrect password"));
		}
		

		return ResponseEntity.ok(Map.of("message", "Login successful", "id", existingDriver.getId()));
	}


	// ========================= UPDATE DRIVER STATUS =========================

	    @Operation(summary = "Update driver status",
	               description = "Updates driver availability (online/offline) and location coordinates.")
	@PutMapping("/api/v1/drivers/{id}/status")
	public ResponseEntity<String> updateDriverStatus(
			@PathVariable("id") String id,
			@RequestBody DriverStatusCoordinatesRequest request) {

		driverServ.updateStatus(id, request.isOnline(), request.getLongitude(), request.getLatitude());

		System.out.println("Driver cordinates" + request);
		System.out.println("Received request: driver isOnline=" + request.isOnline() + ", lat=" + request.getLatitude());

		return ResponseEntity.ok("Drive Status updated");
	}


	// ========================= GET ALL ONLINE DRIVERS =========================

	    @Operation(summary = "Get online drivers",
	               description = "Returns a list of all drivers currently marked as online.")
	@GetMapping("/api/v1/drivers/online")
	public ResponseEntity<List<Driver>> GetIsOnlineDrivers() {
		List<Driver> onlineDrivers = driverServ.getIsOnlineDrivers();
		return ResponseEntity.ok(onlineDrivers);
	}


	// ========================= GET NEARBY DRIVERS =========================

	    @Operation(summary = "Find nearby drivers",
	               description = "Finds drivers near a passenger based on their coordinates.")
	@PostMapping("/api/v1/geo/drivers/nearby")
	public ResponseEntity<List<Driver>> getNearbyDrivers(
			@RequestBody PassengerStatusCoordiantesRequest passengerStatusCoordinatesRequest) {

		double latitude = passengerStatusCoordinatesRequest.getLatitude();
		double longitude = passengerStatusCoordinatesRequest.getLongitude();

		List<Driver> nearbyDrivers = driverServ.findNearbyDrivers(latitude, longitude);
		return ResponseEntity.ok(nearbyDrivers);
	}


	// ========================= EDIT DRIVER INFO =========================

	    @Operation(summary = "Update driver info",
	               description = "Updates driver profile with new values.")
	@PutMapping("/api/v1/drivers/edit/{id}")
	public ResponseEntity<Driver> updateDriver(@PathVariable String id, @RequestBody Driver updatedDriver) {
		Driver driver = driverServ.updateDriver(id, updatedDriver);
		return ResponseEntity.ok(driver);
	}

	// ========================= DELETE DRIVER INFO =========================

	    @Operation(summary = "Delete driver by ID",
	               description = "Deletes a driver record using their ID.")
	@DeleteMapping("/api/v1/drivers/{id}/delete")
	public ResponseEntity<String> deleteDriver(@PathVariable String id){
		boolean deleted = driverServ.deleteDriverById(id);
		if(deleted) {
			return ResponseEntity.ok("Driver deleted successfully");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found");
		}
	}

	// ========================= STRIPE ONBOARDING =========================

	    @Operation(summary = "Stripe onboarding for driver",
	               description = "Starts the Stripe Express onboarding process for a driver using their email and ID.")
	@PostMapping("/api/v1/driver/stripe/onboard")
	public ResponseEntity<String> onboardDriver(@RequestParam String email, @RequestParam String driverId) {
		try {
			System.out.println("🔁 Incoming request to onboard email: " + email + " Driver Id: " + driverId);

			String link = paymentServ.onboardDriver(email, driverId);

			System.out.println("✅ Generated Stripe onboarding link: " + link);
			return ResponseEntity.ok(link);

		} catch (Exception e) {
			System.out.println("❌ Stripe onboarding failed: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(500).body("Error: " + e.getMessage());
		}
	}


	// ========================= GOOGLE SIGN-IN =========================

	    @Operation(summary = "Google Sign-In",
	               description = "Handles Google Sign-In for drivers or passengers, verifies token and role.")
	@PostMapping("/api/v1/auth/signup/{role}/googleId/")
	public ResponseEntity<?> googleSignIn(
			@PathVariable String role,
			@RequestBody Map<String, String> requestBody,
			HttpSession session) {

		try {
			String idToken = requestBody.get("googleId");

			System.out.println("Received Google ID Token: " + idToken);
			System.out.println("Received Role: " + role);

			if (idToken == null || idToken.isEmpty()) {
				System.out.println("Error: Missing or invalid Google ID token");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Google ID token");
			}

			GoogleIdToken.Payload payload = GoogleAuthentication.verifyGoogleToken(idToken);
			System.out.println("Google Token Verification Payload: " + payload);

			if (payload == null) {
				System.out.println("Error: Invalid Google Token");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google Token");
			}

			String email = payload.getEmail();
			String googleId = payload.getSubject();

			System.out.println("Extracted Email: " + email);
			System.out.println("Extracted Google ID: " + googleId);

			// ========== DRIVER LOGIC ==========
			if ("driver".equals(role)) {
				Optional<Driver> existingDriver = driverServ.getDriverByEmail(email);
				System.out.println("Driver Lookup Result: " + existingDriver);

				if (existingDriver.isPresent()) {
					Driver driver = existingDriver.get();
					session.setAttribute("driverId", driver.getId());

					System.out.println("Driver Exists. ID: " + driver.getId());
					
					String token = jwtService.generateToken(driver.getId(),"DRIVER");
					return ResponseEntity.ok(Map.of(
							"exists", true,
							"driverId", driver.getId(),
							"role", "DRIVER",
							"token", token,
							"message", "Driver found, proceed to home."));
				} else {
					System.out.println("New Driver Detected. Redirecting to Registration.");
					return ResponseEntity.ok(Map.of(
							"exists", false,
							"message", "New driver, please proceed to registration."));
				}
			}

			// ========== PASSENGER LOGIC ==========
			if ("passenger".equals(role)) {
				Optional<Passenger> existingPassenger = passengerServ.getPassengerByEmail(email);
				System.out.println("Passenger Lookup Result: " + existingPassenger);

				if (existingPassenger.isPresent()) {
					Passenger passenger = existingPassenger.get();
					session.setAttribute("passengerId", passenger.getId());

					System.out.println("Passenger Exists. ID: " + passenger.getId());
					
					String token = jwtService.generateToken(passenger.getId(), "PASSENGER");
					return ResponseEntity.ok(Map.of(
							"exists", true,
							"passengerId", passenger.getId(),
							"role", "PASSENGER",
							"token", token,
							"message", "Passenger found, proceed to home."));
				} else {
					System.out.println("New Passenger Detected. Redirecting to Registration.");
					return ResponseEntity.ok(Map.of(
							"exists", false,
							"message", "New Passenger, please proceed to registration."));
				}
			}

			System.out.println("Error: Invalid role received - " + role);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role: " + role);

		} catch (IOException e) {
			System.out.println("Google Authentication Failed: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Google authentication failed: " + e.getMessage());

		} catch (Exception e) {
			System.out.println("Unexpected Error: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Google Sign-In Failed: " + e.getMessage());
		}
	}

}
