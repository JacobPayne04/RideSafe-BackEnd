package com.Jacob.ridesafebackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.Jacob.ridesafebackend.service.PassengerService;

//*FOR PASSENGER PROCESS ROUTES*
@Controller
public class PassengerController {
	//TODO need to make passenger service
	@Autowired
	private final PassengerService passengerServe;
	
	public PassengerController(PassengerService passengerServe) {
		this.passengerServe = passengerServe;
	}
	
	
	
	
	
	
	
	
	
}
