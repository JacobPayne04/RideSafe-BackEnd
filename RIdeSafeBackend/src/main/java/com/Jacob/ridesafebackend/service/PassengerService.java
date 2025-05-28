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
		
		/**
		 * Creates a new passenger with a hashed password.
		 */
		public Passenger createPassenger(Passenger passenger) {
			String hashed = BCrypt.hashpw(passenger.getPassword(), BCrypt.gensalt());
			passenger.setPassword(hashed);
			return passengerRepo.save(passenger);
		}


		/**
		 * Returns all passengers in the system.
		 */
		public List<Passenger> getAllPassengers() {
			return passengerRepo.findAll();
		}


		/**
		 * Finds a passenger by ID.
		 */
		public Optional<Passenger> getPassengerById(String id) {
			return passengerRepo.findById(id);
		}


		/**
		 * Retrieves a passenger by email. Logs a warning if multiple are found.
		 */
		public Optional<Passenger> getPassengerByEmail(String email) {
			List<Passenger> passengers = passengerRepo.findAllByEmail(email);

			if (passengers.isEmpty()) {
				return Optional.empty();
			} else if (passengers.size() > 1) {
				System.out.println("Warning: Multiple passengers found with the same email: " + email);
			}

			return Optional.of(passengers.get(0));
		}


		/**
		 * Retrieves a passenger by email. Throws an error if not found.
		 * Used for login and authenticated requests.
		 */
		public Passenger getPassenger(String email) {
			List<Passenger> passengers = passengerRepo.findAllByEmail(email);

			if (passengers.isEmpty()) {
				throw new RuntimeException("Passenger not found with email: " + email);
			}

			return passengers.get(0);
		}


		/**
		 * Verifies if the input password matches the hashed password.
		 */
		public boolean authenticatePassenger(String rawPassword, String hashedPassword) {
			return BCrypt.checkpw(rawPassword, hashedPassword);
		}


		/**
		 * Finds a passenger using either their email or Google ID.
		 */
		public Optional<Passenger> findPassengerByEmailOrGoogleId(String googleId) {
			return passengerRepo.findPassengerByGoogleId(googleId);
		}


		/**
		 * Updates the passenger's location using coordinates (used for mapping or proximity).
		 */
		public void updatePasengerStatus(String id, Double longitude, Double latitude) {
			Passenger passenger = passengerRepo.findById(id)
					.orElseThrow(() -> new RuntimeException("Passneger not found"));

			if (longitude != null & latitude != null) {
				passenger.setLocation(new GeoJsonPoint(longitude, latitude));
				passengerRepo.save(passenger);
			}
		}
		
}
