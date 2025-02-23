package com.Jacob.ridesafebackend.service;

import java.util.Collections;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@Service
public class GoogleAuthentication {
	private final DriverRepository driverRepo;
	private final PassengerRepository passengerRepo;

	
	
	public GoogleAuthentication(DriverRepository driverRepo, PassengerRepository passengerRepo) {
		this.driverRepo = driverRepo;
		this.passengerRepo = passengerRepo;
	}
	
	private static final String CLIENT_ID = "862444779234-9vchmg8lfjgj6stpfbq16bk2k0qrdqqg.apps.googleusercontent.com";

    public static GoogleIdToken.Payload verifyGoogleToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            throw new RuntimeException("Invalid Google Token");
        }
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
	
	public Optional<Driver> loginDriverWithGoogle(String googleId, String idToken){
		if(!validateGoogleToken(idToken)) {
			 throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google Token");
		}
		return driverRepo.findDriverByGoogleId(googleId);
	}
	
	public Optional<Passenger> loginPassengerWithGoogle(String googleId, String idToken){
		if(!validateGoogleToken(idToken)) {
			 throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google Token");
		}
		return passengerRepo.findPassengerByGoogleId(googleId);
	}
	
	
}
