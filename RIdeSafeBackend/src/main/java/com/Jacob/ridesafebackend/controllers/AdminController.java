package com.Jacob.ridesafebackend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.Passenger;
import com.Jacob.ridesafebackend.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Administrative APIs for managing drivers, passengers, and rides") 
public class AdminController {
	
	private final AdminService adminServ;
	
	
	  @Autowired
	  public AdminController(AdminService adminServ) {
	        this.adminServ = adminServ;
	    }

	  		//  Get all drivers who are not allowed to drive
	  		@GetMapping("/api/v1/admin/drivers/unapproved")
	  	    @Operation(summary = "List drivers pending approval")
	  		public ResponseEntity<List<Driver>> getUapprovedDrivers(){
	  			
	  			List<Driver> unapproved = adminServ.getUapprovedDrivers();
	  			
	  			return ResponseEntity.ok(unapproved);
	  		}
	  		
	  		
	  		
	  		 //  Approve a driver by ID
	  		@PutMapping("/api/v1/admin/drivers/{id}/approve")
	  		@Operation(summary = "Approve a driver by ID")
	  		public ResponseEntity<String> approveDriver(@PathVariable String id){
	  			String message = adminServ.approveDriver(id);
	  			return ResponseEntity.ok(message);
	  		}
	  		
	  		
	  		@GetMapping("/api/v1/admin/drivers/all")
	  		@Operation(summary = "List all drivers")
	  		public ResponseEntity<List<Driver>> adminGetAllDrivers() {
	  			List<Driver> driver = adminServ.getAllDrivers();
				return ResponseEntity.ok(driver);
	  		}
	  		
	  		@GetMapping("/api/v1/admin/passengers/all")
	  		@Operation(summary = "List all passengers")
	  		public ResponseEntity<List<Passenger>> adminGetAllPassengers(){
	  			List<Passenger> passenger = adminServ.getAllPassengers();
	  			return ResponseEntity.ok(passenger);
	  		}
	  		
	  		
}
