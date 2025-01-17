package com.Jacob.ridesafebackend.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.RideRepository;

@Controller
public class WebSocketsController {

	   private final SimpMessagingTemplate messagingTemplate;
	    private final RideRepository rideRepo;
	    
	    public WebSocketsController(SimpMessagingTemplate messagingTemplate, RideRepository rideRepo) {
	        this.messagingTemplate = messagingTemplate;
	        this.rideRepo = rideRepo;
	    }
	    
	  
	    @MessageMapping("/ride/request/{driverId}")
	    public void notifyDriver(@DestinationVariable String driverId,Ride ride) {
	    	
	    	  Ride currentRide = rideRepo.findById(ride.getId())
	    	            .orElseThrow(() -> new RuntimeException("Ride not found"));
	   	    
	   	    //notify Driver
	   	    messagingTemplate.convertAndSend("/topic/driver/" + driverId,
	   	    		"newRide request. Please Accept or Deny " + currentRide.getId());
	    }
	    
	    
	    
	    
	    
	    
}
