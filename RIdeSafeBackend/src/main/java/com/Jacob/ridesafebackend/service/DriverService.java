package com.Jacob.ridesafebackend.service;

import java.util.List;

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
		return driverRepo.save(driver);
	}
	
	public List<Driver> getAllDrivers(){
		return driverRepo.findAll();
	}
	
	
	
}
