package com.Jacob.ridesafebackend.service;

import org.springframework.stereotype.Service;

@Service
public class RideService {
			//Connects server to repository
			private final RideService rideRepo; //#TODO create getters and setters !*

			//stating driverRepo refers to driver repository
			public RideService(RideService rideRepo) {
				this.rideRepo = rideRepo;
			}
}
