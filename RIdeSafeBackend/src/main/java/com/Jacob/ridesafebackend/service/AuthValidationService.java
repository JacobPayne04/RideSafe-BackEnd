package com.Jacob.ridesafebackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.exception.UnauthorizedException;

@Service
public class AuthValidationService {


	  @Autowired
	    private jwtService jwtService;

	    public void validateRequest(String token, String userId, String role) {
	        if (token == null || token.isEmpty()) {
	            throw new UnauthorizedException("Missing or invalid token");
	        }

	        // ✅ Token is already clean (no "Bearer " prefix), directly use it
	        String extractedUserId = jwtService.extractUserId(token);
	        String extractedRole = jwtService.extractUserRole(token);

	        if (!jwtService.isTokenValid(token, extractedUserId, extractedRole)) {
	            throw new UnauthorizedException("Invalid token");
	        }

	        if (!extractedUserId.equals(userId) || !extractedRole.equals(role)) {
	            throw new UnauthorizedException("Token claims do not match request");
	        }
	    }	
}
