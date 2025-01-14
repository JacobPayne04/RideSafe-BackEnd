package com.Jacob.ridesafebackend.service;

import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.RideRepository;

@Service
public class RideService {
			//Connects server to repository
			private final RideRepository rideRepo; //#TODO create getters and setters !*

			//stating driverRepo refers to driver repository
			public RideService(RideRepository rideRepo) {
				this.rideRepo = rideRepo;
			}

			public Ride saveRide(Ride ride) {
				return rideRepo.save(ride);
			}
}
