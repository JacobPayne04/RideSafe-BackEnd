package com.Jacob.ridesafebackend.service;

import java.util.Optional;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.Passenger;
import com.Jacob.ridesafebackend.repositorys.DriverRepository;
import com.Jacob.ridesafebackend.repositorys.PassengerRepository;

@Service
public class GoogleAuthentication {
	private final DriverRepository driverRepo;
	private final PassengerRepository passengerRepo;

	
	
	public GoogleAuthentication(DriverRepository driverRepo, PassengerRepository passengerRepo) {
		this.driverRepo = driverRepo;
		this.passengerRepo = passengerRepo;
	}
	
	
	private boolean validateGoogleToken(String idToken) {
		try {
			String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
			RestTemplate restTemplate = new RestTemplate();
			String result = restTemplate.getForObject(url, String.class);
			JSONObject jsonObject = new JSONObject(result);
			return jsonObject.has("email") && jsonObject.has("sub");
			
		} catch (Exception e) {
			return false;
		}
	}
	
	public Optional<Driver> loginDriverWithGoogle(String googelId,String emial, String idToken){
		if(!validateGoogleToken(idToken)) {
			 throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google Token");
		}
		return driverRepo.findDriverByGoogleId(emial, idToken);
	}
	
	public Optional<Passenger> loginPassengerWithGoogle(String googelId,String email, String idToken){
		if(!validateGoogleToken(idToken)) {
			 throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google Token");
		}
		return passengerRepo.findPassengerByGoogleId(email, idToken);
	}
	
	
}
