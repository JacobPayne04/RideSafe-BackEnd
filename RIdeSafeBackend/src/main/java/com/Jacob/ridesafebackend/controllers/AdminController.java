package com.Jacob.ridesafebackend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.service.AdminService;

@RestController
public class AdminController {
	
	private final AdminService adminServ;
	
	  @Autowired
	  public AdminController(AdminService adminServ) {
	        this.adminServ = adminServ;
	    }

	  		// ✅ Get all drivers who are not allowed to drive
	  		@GetMapping("/drivers/unapproved")
	  		public ResponseEntity<List<Driver>> getUapprovedDrivers(){
	  			
	  			List<Driver> unapproved = adminServ.getUapprovedDrivers();
	  			
	  			return ResponseEntity.ok(unapproved);
	  		}
	  		
	  		
	  		
	  		 // ✅ Approve a driver by ID
	  		@PutMapping("/approve/driver/{id}")
	  		public ResponseEntity<String> approveDriver(@PathVariable String id){
	  			String message = adminServ.approveDriver(id);
	  			return ResponseEntity.ok(message);
	  		}
}
