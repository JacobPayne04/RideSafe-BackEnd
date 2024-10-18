package com.Jacob.ridesafebackend.service;

import org.springframework.stereotype.Service;

@Service
public class PassengerService {
		//Connects server to repository
		private final PassengerService passengerRepo; //#TODO create getters and setters !*

		//stating driverRepo refers to driver repository
		public PassengerService(PassengerService passengerRepo) {
			this.passengerRepo = passengerRepo;
		}

}
