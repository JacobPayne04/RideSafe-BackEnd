package com.Jacob.ridesafebackend.controllers;
import java.util.List;
import java.util.Optional;

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
import com.Jacob.ridesafebackend.service.DriverService;

import jakarta.servlet.http.HttpSession;

//*FOR DRIVER PROCESS ROUTES*
@CrossOrigin(origins = "http://localhost:3000")
@RestController // Changed from Controller to RestController
public class DriverController {
	
	//TODO need to make driver service
	@Autowired
	private final DriverService driverServ;
	
	public DriverController(DriverService driverServ) {
		this.driverServ = driverServ;
	}
	

	@PostMapping("/new")
	public ResponseEntity<Driver> createDriver(@RequestBody Driver driver,HttpSession session){
		
		Driver creatDriver = driverServ.creatDriver(driver);
		
		session.setAttribute("driver_id", creatDriver.getId());
		
		return ResponseEntity.ok(creatDriver);
	}
	
	
	
	
	
	//TODO incomplete fetching driver by id in session 
	@PostMapping("/new/process")
	public ResponseEntity<?> processDriver(HttpSession session){
		
		String driverId  = (String) session.getAttribute("driver_id");
		
		if(driverId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Driver not logged in, Please sing up to be a driver");
		}
		
		//Driver oneDriver = driverServ.getDriverById(driverId);
	
		return ResponseEntity.ok(oneDriver);
	}
	
	
	
	
	
	
	@GetMapping("/drivers") // Fixed the get All Drivers Route
	public ResponseEntity<List<Driver>> getAllDrivers(){	
		List<Driver> drivers = driverServ.getAllDrivers();
		return ResponseEntity.ok(drivers);
	}
	
	// Getting One Driver
	@GetMapping("/driver/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable("id") String id) {	// Pass in the drivers Id to send to the frontend
		
        Optional<Driver> driver = driverServ.getDriverById(id); // fetching a driver by its id

        if (driver.isEmpty()) {	// if the driver does not exist, return "Driver not found."
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found.");
        }

        return ResponseEntity.ok(driver.get()); // return the Driver 
    }
	
	
	
	
	
	
	
	
	
	
	
	

}
