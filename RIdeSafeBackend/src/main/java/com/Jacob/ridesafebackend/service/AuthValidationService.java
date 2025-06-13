package com.Jacob.ridesafebackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.exception.UnauthorizedException;

@Service
public class AuthValidationService {


	@Autowired
	private jwtService jwtService;
	
    public void validateRequest(String token, String userId, String role) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String jwt = token.substring(7); // Remove "Bearer " prefix

        // Validate and extract data
        String extractedUserId = jwtService.extractUserId(jwt);
        String extractedRole = jwtService.extractUserRole(jwt);

        if (!jwtService.isTokenValid(jwt, extractedUserId, extractedRole)) {
            throw new UnauthorizedException("Invalid token");
        }

        if (!extractedUserId.equals(userId) || !extractedRole.equals(role)) {
            throw new UnauthorizedException("Token claims do not match request");
        }
    }
	
	
	
	
	
	
	
	
	
	
}
