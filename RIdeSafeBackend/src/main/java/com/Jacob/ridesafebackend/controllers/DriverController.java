package com.Jacob.ridesafebackend.controllers;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.controllers.PassengerController.GoogleLoginRequest;
import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.LoginDriver;
import com.Jacob.ridesafebackend.service.DriverService;
import com.Jacob.ridesafebackend.service.GoogleAuthentication;

import jakarta.servlet.http.HttpSession;

//*FOR DRIVER PROCESS ROUTES*
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController // Changed from Controller to RestController
public class DriverController {
	
	//TODO need to make driver service
	@Autowired
	private final DriverService driverServ;
	private final GoogleAuthentication GoogleAuth;

	public DriverController(DriverService driverServ,GoogleAuthentication GoogleAuth) {
		this.driverServ = driverServ;
		this.GoogleAuth = GoogleAuth;
	}
	

	@PostMapping("/new")
	public ResponseEntity<Driver> createDriver(@RequestBody Driver driver,HttpSession session){
		
		Driver creatDriver = driverServ.creatDriver(driver);
		
		return ResponseEntity.ok(creatDriver);
	}
	
	
	//Current Driver in session route
	@GetMapping("/drivers") 
	public ResponseEntity<List<Driver>> getAllDrivers(){	
		List<Driver> drivers = driverServ.getAllDrivers();
		return ResponseEntity.ok(drivers);
	}
	
	// Getting One Driver
	@GetMapping("/driver/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable("id") String id)  {	// Pass in the drivers Id to send to the frontend
		
        Optional<Driver> driver = driverServ.getDriverById(id); // fetching a driver by its id

        if (driver.isEmpty()) {	// if the driver does not exist, return "Driver not found."
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found.");
        }

        return ResponseEntity.ok(driver.get()); // return the Driver 
    }
	
	
	   @PostMapping("/login")
	    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDriver loginDriver,HttpSession session) {
	        // Fetch the driver by email
	        Driver existingDriver = driverServ.getDriver(loginDriver.getEmail());
	        
	        if (existingDriver == null) {
	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of("message", "Unknown email"));
	        }

	        // Check the password using BCrypt
	        if (!BCrypt.checkpw(loginDriver.getPassword(), existingDriver.getPassword())) {
	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of("message", "Incorrect password"));
	        }
	   
	        // Login successful
	        return ResponseEntity.ok(Map.of("message", "Login successful","id",existingDriver.getId()));
	      
	    }
	   
	   //ROUTES FOR DRIVER FUNCTIONALITY*******************************************************************************************
	   
	   @PutMapping("/{id}/status")
	   public ResponseEntity<String> updateDriverStatus(@PathVariable("id") String id,@RequestParam boolean isOnline){
		   driverServ.updateStatus(id,isOnline);
		   return ResponseEntity.ok("Drive Status updated");
	   }
	   
	   
	   @GetMapping("/online/drivers")
	   public ResponseEntity<List<Driver>>GetIsOnlineDrivers(){
		   List<Driver> onlineDrivers = driverServ.getIsOnlineDrivers();
		   return ResponseEntity.ok(onlineDrivers);
		  
	   }
	   
	   @PostMapping("/signup/driver/googleId")
		  public ResponseEntity<?> googleSingIn(@RequestBody GoogleLoginRequest  request){
			Optional<Driver> driver = GoogleAuth.loginDriverWithGoogle(
					request.googleId(), request.email(),request.idToken());
			
			if(driver.isPresent()) {
				return ResponseEntity.ok(driver.get());
			} else {
				return ResponseEntity.status(404).body("Redirect: Complete passenger registration");
			}
			
		 }
	
	   
	

}
