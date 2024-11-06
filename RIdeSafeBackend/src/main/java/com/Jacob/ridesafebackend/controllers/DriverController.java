package com.Jacob.ridesafebackend.controllers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.service.DriverService;

import jakarta.servlet.http.HttpSession;

//*FOR DRIVER PROCESS ROUTES*
@CrossOrigin(origins = "http://localhost:3000")
@Controller
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
	
	
	@GetMapping("/drivers")
	public List<Driver> getAllDrivers(@RequestBody Driver driver,HttpSession session){	
		return driverServ.getAllDrivers();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
