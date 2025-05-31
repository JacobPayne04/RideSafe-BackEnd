package com.Jacob.ridesafebackend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Jacob.ridesafebackend.dto.DriverRequiredInformationDTO;
import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.repositorys.DriverMongoRepository;
import com.Jacob.ridesafebackend.repositorys.DriverRepository;

@Service
public class DriverService {
	// Connects server to repository
	private final DriverRepository driverRepo; // #TODO create getters and setters !*
	private final DriverMongoRepository driverMongoRepo;

	// stating driverRepo refers to driver repository

	public DriverService(DriverRepository driverRepo, DriverMongoRepository driverMongoRepo) {

		this.driverRepo = driverRepo;
		this.driverMongoRepo = driverMongoRepo;
	}

	/**
	 * Creates a new driver. Hashes the password if provided before saving.
	 */
	public Driver creatDriver(Driver driver) {
		if (driver.getPassword() != null && !driver.getPassword().isEmpty()) {
			String hashed = BCrypt.hashpw(driver.getPassword(), BCrypt.gensalt());
			driver.setPassword(hashed);
		} else {
			driver.setPassword(null);
		}
		return driverRepo.save(driver);
	}

	// TODO: Replace this Google API key with one from the real ride share account
	
	public Driver save(Driver driver) {
        return driverRepo.save(driver);
    }

	/**
	 * Handles full sign up data for a driver, including saving documents and info updates.
	 */
	public void processDriverRequiredInformationSignup(
			DriverRequiredInformationDTO info,
			MultipartFile dlFile,
			MultipartFile studentIdFile
	) throws IOException {

		String driverId = info.getDriverid();
		Optional<Driver> optionalDriver = driverRepo.findById(driverId);

		if (optionalDriver.isEmpty()) {
			throw new RuntimeException("Driver not found");
		}

		Driver driver = optionalDriver.get();

		// Save file uploads and set paths
		String dlPath = saveFile(dlFile);
		String studentIdPath = saveFile(studentIdFile);

		driver.setDlFileUrl(dlPath);
		driver.setStudentIdFileUrl(studentIdPath);

		// Update driver profile info
		driver.setFirstName(info.getFirstName());
		driver.setLastName(info.getLastName());
		driver.setLicensePlate(info.getLicensePlate());
		driver.setAcceptedTerms(info.isAcceptedTerms());
		driver.seteSign(info.geteSign());

		driverRepo.save(driver);
	}


	/**
	 * Returns all drivers from the repository.
	 */
	public List<Driver> getAllDrivers() {
		return driverRepo.findAll();
	}


	/**
	 * Returns a single driver by their ID.
	 */
	public Optional<Driver> getDriverById(String id) {
		return driverRepo.findById(id);
	}


	/**
	 * Returns a driver by email, or empty if not found.
	 * Logs a warning if multiple drivers are found.
	 */
	public Optional<Driver> getDriverByEmail(String email) {
		List<Driver> drivers = driverRepo.findAllByEmail(email);

		if (drivers.isEmpty()) {
			return Optional.empty();
		} else if (drivers.size() > 1) {
			System.out.println("Warning: Multiple drivers found with the same email: " + email);
		}

		return Optional.of(drivers.get(0));
	}


	/**
	 * Gets a driver by email. Throws an exception if not found.
	 * Used for login.
	 */
	public Driver getDriver(String email) {
		List<Driver> drivers = driverRepo.findAllByEmail(email);

		if (drivers.isEmpty()) {
			throw new RuntimeException("Driver not found with email: " + email);
		}

		return drivers.get(0);
	}


	/**
	 * Verifies if the raw password matches the stored hashed password.
	 */
	public boolean authenticateDriver(String rawPassword, String hashedPassword) {
		return BCrypt.checkpw(rawPassword, hashedPassword);
	}


	/**
	 * Updates a driver's online status and sets coordinates if available.
	 */
	public void updateStatus(String id, boolean isOnline, Double longitude, Double latitude) {
		Driver driver = driverRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Driver not found"));

		driver.setIsOnline(isOnline);
		System.out.println("Saving driver with isOnline = " + driver.isOnline());

		if (isOnline && longitude != null && latitude != null) {
			driver.setLocation(new GeoJsonPoint(longitude, latitude));
		}

		driverRepo.save(driver);
	}


	/**
	 * Returns all drivers currently marked as online.
	 */
	public List<Driver> getIsOnlineDrivers() {
		return driverMongoRepo.findByIsOnlineTrue();
	}


	/**
	 * Finds a driver by their Google ID if available.
	 */
	public Optional<Driver> findDriverGoogleId(String googleId) {
		return driverRepo.findDriverByGoogleId(googleId);
	}


	/**
	 * Returns a list of drivers near a given location.
	 */
	public List<Driver> findNearbyDrivers(double latitude, double longitude) {
		return driverRepo.findDriversNearLocation(longitude, latitude); // Note: longitude first
	}


	/**
	 * Updates an existing driver with new values, only if new data is provided.
	 */
	public Driver updateDriver(String id, Driver updatedDriver) {
		Driver existingDriver = driverRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Driver not found with ID: " + id));

		// Update first name
		if (updatedDriver.getFirstName() != null && !updatedDriver.getFirstName().isEmpty()) {
			existingDriver.setFirstName(updatedDriver.getFirstName());
		}

		// Update last name
		if (updatedDriver.getLastName() != null && !updatedDriver.getLastName().isEmpty()) {
			existingDriver.setLastName(updatedDriver.getLastName());
		}

		// Update email
		if (updatedDriver.getEmail() != null && !updatedDriver.getEmail().isEmpty()) {
			existingDriver.setEmail(updatedDriver.getEmail());
		}

		// Update license plate
		if (updatedDriver.getLicensePlate() != null && !updatedDriver.getLicensePlate().isEmpty()) {
			existingDriver.setLicensePlate(updatedDriver.getLicensePlate());
		}

		// Update Google ID
		if (updatedDriver.getGoogleId() != null && !updatedDriver.getGoogleId().isEmpty()) {
			existingDriver.setGoogleId(updatedDriver.getGoogleId());
		}

		// Update driver rate (must be > 0)
		if (updatedDriver.getDriverRate() > 0) {
			existingDriver.setDriverRate(updatedDriver.getDriverRate());
		}

		// Hash and update password if provided
		if (updatedDriver.getPassword() != null && !updatedDriver.getPassword().isEmpty()) {
			String hashed = BCrypt.hashpw(updatedDriver.getPassword(), BCrypt.gensalt());
			existingDriver.setPassword(hashed);
		}

		return driverRepo.save(existingDriver);
	}


	/**
	 * Saves an uploaded file to disk and returns its relative path.
	 */
	private String saveFile(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IOException("File is empty");
		}

		String uploadsDir = "uploads/";
		Files.createDirectories(Paths.get(uploadsDir));

		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		Path filePath = Paths.get(uploadsDir + fileName);

		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		return "/" + uploadsDir + fileName;
	}

}
