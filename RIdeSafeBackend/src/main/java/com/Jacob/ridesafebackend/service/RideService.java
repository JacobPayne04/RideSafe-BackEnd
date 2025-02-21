package com.Jacob.ridesafebackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.Driver;
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

	public void updateRideStatus(String id, RideStatus status) { // #TODO Make this update ride for ONGOING
		Ride ride = rideRepo.findById(id).orElseThrow(() -> new RuntimeException("Ride not found"));
		ride.setRideStatus(status);
		rideRepo.save(ride);
	}

	public Ride acceptRide(String rideId, String driverId) {
		// Retrieve the ride by ID
		Optional<Ride> optionalRide = rideRepo.findById(rideId);

		if (optionalRide.isPresent()) {
			Ride ride = optionalRide.get();

			// Set the driver ID
			ride.setDriverId(driverId);

			// Check if there is already an ongoing ride for the driver
			List<Ride> ongoingRides = rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.ONGOING);

			if (!ongoingRides.isEmpty()) {
				// If there is an ongoing ride, set this one to INQUEUE
				ride.setRideStatus(Ride.RideStatus.INQUEUE);

				// Get the current queue length and set the position
				Long queueCount = rideRepo.countByDriverIdAndStatus(driverId, Ride.RideStatus.INQUEUE);
				ride.setQueuePosition(queueCount.intValue() + 1);
			} else {
				// If no ongoing ride, set this one as ONGOING
				ride.setRideStatus(Ride.RideStatus.ONGOING);
				ride.setQueuePosition(0);
			}

			// Save the updated ride
			return rideRepo.save(ride);
		}

		return null; // Return null if the ride isn't found
	}

	public Ride completeRide(String rideId, String driverId) {

		Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));

		ride.setRideStatus(Ride.RideStatus.COMPLETED);

		rideRepo.save(ride);

		List<Ride> inQueueRides = rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.INQUEUE);

		if (!inQueueRides.isEmpty()) {
			Ride nextRide = inQueueRides.get(0);

			nextRide.setRideStatus(Ride.RideStatus.ONGOING);

			nextRide.setQueuePosition(0);

			rideRepo.save(nextRide);

			for (int i = 1; i < inQueueRides.size(); i++) {
				Ride queueRide = inQueueRides.get(i);

				queueRide.setQueuePosition(i);
				rideRepo.save(queueRide);
			}
		}

		return ride;
	}

	
	public List<Ride> getOngoingRidesByDriverId(String driverId) {
		return rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.ONGOING);
	}

	// new method********************

	public String getGoogleMapsUrl(String id) {
		Ride ride = rideRepo.findById(id).orElseThrow(() -> new RuntimeException("Ride not found wiht ID: " + id));

		return String.format(GOOGLE_MAPS_DIRECTIONS_URL, ride.getFromLatitude(), ride.getFromLongitude(),
				ride.getToLatitude(), ride.getToLongitude(), apiKey);

	}
	
	

}
