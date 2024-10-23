package com.Jacob.ridesafebackend.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.service.DriverService;

import jakarta.servlet.http.HttpSession;

//*FOR DRIVER PROCESS ROUTES*
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
		if(session.getAttribute("user_id") == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Driver creatDriver = driverServ.creatDriver(driver);
		
		return ResponseEntity.ok(creatDriver);
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
