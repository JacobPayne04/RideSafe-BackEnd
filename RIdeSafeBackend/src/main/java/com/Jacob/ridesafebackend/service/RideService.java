package com.Jacob.ridesafebackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.models.Ride.RideStatus;
import com.Jacob.ridesafebackend.repositorys.RideRepository;

@Service
public class RideService {
	// Connects server to repository
	private final RideRepository rideRepo; // #TODO create getters and setters !*
	
	@Value("${google.maps.api.key}")
    private String apiKey;
	
	private static final String GOOGLE_MAPS_DIRECTIONS_URL = "https://www.google.com/maps/dir/?api=1&origin=%f,%f&destination=%f,%f";

	// stating driverRepo refers to driver repository
	public RideService(RideRepository rideRepo) {
		this.rideRepo = rideRepo;
	}

	public Ride saveRide(Ride ride) {
		return rideRepo.save(ride);
	}

	public Optional<Ride> getRideById(String id) {
		return rideRepo.findById(id);
	}

	public void updateRideStatus(String id, RideStatus status) {
		Ride ride = rideRepo.findById(id).orElseThrow(() -> new RuntimeException("Ride not found"));

		ride.setRideStatus(status);
		rideRepo.save(ride);
	}

	public List<Ride> getOngoingRidesByDriverId(String driverId) {
		return rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.ONGOING);
	}
	
	//new method********************

	public String getGoogleMapsUrl(String id) {
   	 Ride ride = rideRepo.findById(id)
		    	.orElseThrow(() -> new RuntimeException("Ride not found wiht ID: " + id));
		    	
		   return String.format(GOOGLE_MAPS_DIRECTIONS_URL,ride.getFromLatitude(),ride.getFromLongitude(), ride.getToLatitude(), ride.getToLongitude(), apiKey);
		    	
				
   }


}
