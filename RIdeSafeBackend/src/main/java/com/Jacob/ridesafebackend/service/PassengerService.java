package com.Jacob.ridesafebackend.service;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;


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
		

		 // Retrieve a driver by email
	    public Passenger getPassenger(String email) {
	        return passengerRepo.findByEmail(email);
	    }

	    // Authenticate a driver by verifying their password
	    public boolean authenticatePassenger(String rawPassword, String hashedPassword) {
	        return BCrypt.checkpw(rawPassword, hashedPassword);
	    }
		
	    public Optional<Passenger> findPassengerByEmailOrGoogleId(String googleId){
	    	return passengerRepo.findPassengerByGoogleId(googleId);
	    }

		
		
}
