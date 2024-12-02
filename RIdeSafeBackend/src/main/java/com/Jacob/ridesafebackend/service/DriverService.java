package com.Jacob.ridesafebackend.service;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.repositorys.DriverRepository;

@Service
public class DriverService {
	//Connects server to repository
	private final DriverRepository driverRepo; //#TODO create getters and setters !*

	//stating driverRepo refers to driver repository
	public DriverService(DriverRepository driverRepo) {
		this.driverRepo = driverRepo;
	}
	
	public Driver creatDriver(Driver driver) {
		String hashed = BCrypt.hashpw(driver.getPassword(), BCrypt.gensalt());
		driver.setPassword(hashed);
		return driverRepo.save(driver);
	}
	
	public List<Driver> getAllDrivers(){
		return driverRepo.findAll();
	}
	//added get driver by id route
	public Optional<Driver> getDriverById(String id){
		return driverRepo.findById(id);
	}
	

	 // Retrieve a driver by email
    public Driver getDriver(String email) {
        return driverRepo.findByEmail(email);
    }

    // Authenticate a driver by verifying their password
    public boolean authenticateDriver(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
    
    public void updateStatus(String id, boolean isOnline) {
    	Driver driver = driverRepo.findById(id).
    			orElseThrow(()-> new RuntimeException("Driver not found"));
    	driver.setIsOnline(isOnline);
    	driverRepo.save(driver);
    }

	
    
    
}

