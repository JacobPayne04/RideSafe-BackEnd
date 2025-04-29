package com.Jacob.ridesafebackend.service;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.Passenger;
import com.Jacob.ridesafebackend.repositorys.PassengerRepository;

@Service
public class PassengerService {
		//Connects server to repository
		private final PassengerRepository passengerRepo; //#TODO create getters and setters !*

		//stating driverRepo refers to driver repository
		public PassengerService(PassengerRepository passengerRepo) {
			this.passengerRepo = passengerRepo;
		}
		
		public Passenger createPassenger(Passenger passenger) {
			String hashed = BCrypt.hashpw(passenger.getPassword(), BCrypt.gensalt());
			passenger.setPassword(hashed);
			return passengerRepo.save(passenger);
		}
		
		public List<Passenger> getAllPassengers(){
			return passengerRepo.findAll();
		}
		//added get driver by id route
		public Optional<Passenger> getPassengerById(String id){
			return passengerRepo.findById(id);
		}
		
		// Retrieve a passenger by email ??******************************* THIS IS NEW
		public Optional<Passenger> getPassengerByEmail(String email) {
		    // Use a repository method that returns a List of drivers with the given email
		    List<Passenger> passengers = passengerRepo.findAllByEmail(email);
		    if (passengers.isEmpty()) {
		        return Optional.empty();
		    } else if (passengers.size() > 1) {
		        System.out.println("Warning: Multiple drivers found with the same email: " + email);
		    }
		    return Optional.of(passengers.get(0));
		}

		// ✅ Used for regular (throws an error if not found)
		public Passenger getPassenger(String email) {
		    List<Passenger> passengers = passengerRepo.findAllByEmail(email);
		    if (passengers.isEmpty()) {
		        throw new RuntimeException("Passenger not found with email: " + email);
		    }
		    return passengers.get(0);  // Returns the first driver found
		}

	    // Authenticate a driver by verifying their password
	    public boolean authenticatePassenger(String rawPassword, String hashedPassword) {
	        return BCrypt.checkpw(rawPassword, hashedPassword);
	    }
		
	    public Optional<Passenger> findPassengerByEmailOrGoogleId(String googleId){
	    	return passengerRepo.findPassengerByGoogleId(googleId);
	    }

		
	    
	    //new method to get passenger coordinates
		public void updatePasengerStatus(String id, Double longitude, Double latitude) {
			Passenger passenger = passengerRepo.findById(id).orElseThrow(()-> new RuntimeException("Passneger not found"));
			
			if(longitude != null & latitude != null) {
				passenger.setLocation(new GeoJsonPoint(longitude, latitude));  
				passengerRepo.save(passenger);
			}
		}
		
}
