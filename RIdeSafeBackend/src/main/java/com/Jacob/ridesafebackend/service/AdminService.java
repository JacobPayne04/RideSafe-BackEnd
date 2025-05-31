package com.Jacob.ridesafebackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.Passenger;
import com.Jacob.ridesafebackend.repositorys.DriverRepository;
import com.Jacob.ridesafebackend.repositorys.PassengerRepository;

@Service
public class AdminService {

	
	private final DriverRepository driverRepo;
	private final PassengerRepository passengerRepo;
	
	@Autowired
	public AdminService(DriverRepository driverRepo ,PassengerRepository passengerRepo) {
	this.driverRepo = driverRepo;	
	this.passengerRepo = passengerRepo;
	}
	
	
	/**
	 * Gets un approved drivers to render for the admin panel to accept them
	 */
	public List<Driver> getUapprovedDrivers(){
		return  driverRepo.findByIsAllowedToDriveFalse();
	}
	
	/**
	 * Approves drivers
	 */
	
	public String approveDriver(String id) {
		Optional<Driver>  optionalDriver = driverRepo.findById(id);
		if(!optionalDriver.isPresent()) {
			throw new RuntimeException("Driver not found with ID: " + id);
		}
		
		Driver driver = optionalDriver.get();
		driver.setAllowedToDriver(true);
		driverRepo.save(driver);
		
		return "driver approved successfully: ";
	}
	
	/**
	 * Retrieves all driver info
	 */
	public List<Driver> getAllDrivers(){
		return driverRepo.findAll();
	}
	
	
	
	
	/**
	 * Retrieve all passenger info
	 */
	
	public List<Passenger> getAllPassengers(){
		return passengerRepo.findAll();
	}
	
}
