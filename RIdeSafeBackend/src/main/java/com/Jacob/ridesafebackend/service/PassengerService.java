package com.Jacob.ridesafebackend.service;

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
			return passengerRepo.save(passenger);
		}

}
