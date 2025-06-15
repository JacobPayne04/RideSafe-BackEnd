package com.Jacob.ridesafebackend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.models.Ride.RideStatus;
import com.Jacob.ridesafebackend.repositorys.RideRepository;


@Service
public class RideService {
	// Connects server to repository
	
	private final RideRepository rideRepo; 
	private final SimpMessagingTemplate messagingTemplate;

	@Value("${google.maps.api.key}")
	private String apiKey;

	private static final String GOOGLE_MAPS_DIRECTIONS_URL = "https://www.google.com/maps/dir/?api=1&origin=%f,%f&destination=%f,%f";


	// driverRepo refers to ride repository
	public RideService(RideRepository rideRepo ,SimpMessagingTemplate messagingTemplate) {
		this.rideRepo = rideRepo;
		this.messagingTemplate = messagingTemplate;
	}

	/**
	 * Saves a new ride to the database.
	 */
	public Ride saveRide(Ride ride) {
		return rideRepo.save(ride);
	}

	/**
	 * Retrieves a ride by its unique ID.
	 */
	public Optional<Ride> getRideById(String id) {
		return rideRepo.findById(id);
	}

	/**
	 * Gets the driverId assigned to a ride.
	 */
	public Optional<String> getDriverIdByRideId(String rideId) {
		Optional<Ride> rideOptional = rideRepo.findById(rideId);
		return rideOptional.map(Ride::getDriverId);
	}
	
	/**
	 * Gets the ride id and subscribes the passenger to message for rating system
	 */
	public void sendPassengerRatingPrompt(String rideId) {
	    Optional<Ride> optionalRide = rideRepo.findById(rideId);
	    if (!optionalRide.isPresent()) {
	        throw new RuntimeException("Ride not found with ID: " + rideId);
	    }

	    Ride ride = optionalRide.get();
	    String passengerId = ride.getPassengerId();
	    String driverId = ride.getDriverId();

	    Map<String, String> payload = new HashMap<>();
	    payload.put("type", "RIDE_ENDED");
	    payload.put("driverId", driverId);

	    messagingTemplate.convertAndSend("/topic/passenger/" + passengerId, payload);
	}
	
	

	/**
	 * Updates the ride status (e.g., to ONGOING, COMPLETED).
	 * #TODO: Integrate this into live state transitions like driver actions.
	 */
	public void updateRideStatus(String id, RideStatus status) {
		Ride ride = rideRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Ride not found"));
		ride.setRideStatus(status);
		rideRepo.save(ride);
	}

	/**
	 * Assigns a driver to a ride.
	 * If the driver has an ongoing ride, places this ride in queue.
	 * If not, marks the ride as ONGOING immediately.
	 */
	public Ride acceptRide(String rideId, String driverId) {
		Optional<Ride> optionalRide = rideRepo.findById(rideId);

		if (optionalRide.isPresent()) {
			Ride ride = optionalRide.get();
			ride.setDriverId(driverId);

			// Check if driver already has an ongoing ride
			List<Ride> ongoingRides = rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.ONGOING);

			if (!ongoingRides.isEmpty()) {
				// Add this ride to the driver's queue
				ride.setRideStatus(Ride.RideStatus.INQUEUE);
				Long queueCount = rideRepo.countByDriverIdAndStatus(driverId, Ride.RideStatus.INQUEUE);
				ride.setQueuePosition(queueCount.intValue() + 1);
			} else {
				// No current ride, so start this one
				ride.setRideStatus(Ride.RideStatus.ONGOING);
				ride.setQueuePosition(0);
			}

			return rideRepo.save(ride);
		}

		// Ride not found
		return null;
	}

	/**
	 * Completes the current ride and promotes the next one in queue to ONGOING.
	 */
	public Ride completeRide(String rideId, String driverId) {
		Ride ride = rideRepo.findById(rideId)
				.orElseThrow(() -> new RuntimeException("Ride not found"));

		ride.setRideStatus(Ride.RideStatus.COMPLETED);
		rideRepo.save(ride);

		// Check if driver has queued rides
		List<Ride> inQueueRides = rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.INQUEUE);

		if (!inQueueRides.isEmpty()) {
			// Promote the next ride to ONGOING
			Ride nextRide = inQueueRides.get(0);
			nextRide.setRideStatus(Ride.RideStatus.ONGOING);
			nextRide.setQueuePosition(0);
			rideRepo.save(nextRide);

			// Reassign queue positions to remaining rides
			for (int i = 1; i < inQueueRides.size(); i++) {
				Ride queueRide = inQueueRides.get(i);
				queueRide.setQueuePosition(i);
				rideRepo.save(queueRide);
			}
		}

		return ride;
	}

	/**
	 * Retrieves all ongoing rides currently assigned to a driver.
	 */
	public List<Ride> getOngoingRidesByDriverId(String driverId) {
		return rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.ONGOING);
	}

	/**
	 * Generates a Google Maps directions URL for a ride.
	 * Takes the ride’s coordinates and embeds them into the format URL.
	 */
	public String getGoogleMapsUrl(String id) {
		Ride ride = rideRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Ride not found wiht ID: " + id));

		return String.format(
				GOOGLE_MAPS_DIRECTIONS_URL,
				ride.getFromLatitude(),
				ride.getFromLongitude(),
				ride.getToLatitude(),
				ride.getToLongitude(),
				apiKey
		);
	}
}

