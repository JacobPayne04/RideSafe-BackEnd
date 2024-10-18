package com.Jacob.ridesafebackend.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.Jacob.ridesafebackend.service.DriverService;

//*FOR DRIVER PROCESS ROUTES*
@Controller
public class DriverController {
	
	//TODO need to make driver service
	@Autowired
	private final DriverService driverServ;
	
	public DriverController(DriverService driverServ) {
		this.driverServ = driverServ;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
