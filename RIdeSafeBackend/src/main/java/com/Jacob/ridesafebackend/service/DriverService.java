package com.Jacob.ridesafebackend.service;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

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

	public Driver creatDriver(Driver driver) {

		if (driver.getPassword() != null && !driver.getPassword().isEmpty()) {
			String hashed = BCrypt.hashpw(driver.getPassword(), BCrypt.gensalt());
			driver.setPassword(hashed);
		} else {
			driver.setPassword(null);
		}

		return driverRepo.save(driver);
	}

	public List<Driver> getAllDrivers() {
		return driverRepo.findAll();
	}

	// added get driver by id route
	public Optional<Driver> getDriverById(String id) {
		return driverRepo.findById(id);
	}

	// Retrieve a driver by email ??******************************* THIS IS NEW
	public Optional<Driver> getDriverByEmail(String email) {
	    // Use a repository method that returns a List of drivers with the given email
	    List<Driver> drivers = driverRepo.findAllByEmail(email);
	    if (drivers.isEmpty()) {
	        return Optional.empty();
	    } else if (drivers.size() > 1) {
	        System.out.println("Warning: Multiple drivers found with the same email: " + email);
	    }
	    return Optional.of(drivers.get(0));
	}

	// ✅ Used for regular login (throws an error if not found)
	public Driver getDriver(String email) {
	    List<Driver> drivers = driverRepo.findAllByEmail(email);
	    if (drivers.isEmpty()) {
	        throw new RuntimeException("Driver not found with email: " + email);
	    }
	    return drivers.get(0);  // Returns the first driver found
	}


	// Authenticate a driver by verifying their password
	public boolean authenticateDriver(String rawPassword, String hashedPassword) {
		return BCrypt.checkpw(rawPassword, hashedPassword);
	}

	public void updateStatus(String id, boolean isOnline,Double longitude,Double latitude) {
		Driver driver = driverRepo.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
		driver.setIsOnline(isOnline);
		System.out.println("Saving driver with isOnline = " + driver.isOnline());

		if(isOnline && longitude != null && latitude != null) {
			driver.setLocation(new GeoJsonPoint(longitude,latitude));
		}
		driverRepo.save(driver);
	}

	public List<Driver> getIsOnlineDrivers() {
		return driverMongoRepo.findByIsOnlineTrue();
	}

	public Optional<Driver> findDriverGoogleId(String googleId) {
		return driverRepo.findDriverByGoogleId(googleId);
	}
	
	public List<Driver> findNearbyDrivers(double latitude, double longitude) {
	    return driverRepo.findDriversNearLocation(longitude, latitude); // ✅ correct order
	}
	
	
	
	

	public Driver updateDriver(String id, Driver updatedDriver) {
		Driver existingDriver = driverRepo.findById(id)
		        .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + id));

		    // Update fields if new values are provided
		    if (updatedDriver.getFirstName() != null && !updatedDriver.getFirstName().isEmpty()) {
		        existingDriver.setFirstName(updatedDriver.getFirstName());
		    }
		    
		    if (updatedDriver.getLastName() != null && !updatedDriver.getLastName().isEmpty()) {
		        existingDriver.setLastName(updatedDriver.getLastName());
		    }
		    
		    if (updatedDriver.getEmail() != null && !updatedDriver.getEmail().isEmpty()) {
		        existingDriver.setEmail(updatedDriver.getEmail());
		    }

		    if (updatedDriver.getLicensePlate() != null && !updatedDriver.getLicensePlate().isEmpty()) {
		        existingDriver.setLicensePlate(updatedDriver.getLicensePlate());
		    }

		    if (updatedDriver.getGoogleId() != null && !updatedDriver.getGoogleId().isEmpty()) {
		        existingDriver.setGoogleId(updatedDriver.getGoogleId());
		    }

		    if (updatedDriver.getDriverRate() > 0) {
		        existingDriver.setDriverRate(updatedDriver.getDriverRate());
		    }

		    // Update password only if a new password is provided
		    if (updatedDriver.getPassword() != null && !updatedDriver.getPassword().isEmpty()) {
		        String hashed = BCrypt.hashpw(updatedDriver.getPassword(), BCrypt.gensalt());
		        existingDriver.setPassword(hashed);
		    }

		    return driverRepo.save(existingDriver);
	}



}
