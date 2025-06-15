package com.Jacob.ridesafebackend.service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class jwtService {

	
	
	private final String SECRETE_KEY = "your-256-bit-secret-key-goes-here-make-it-long";

	
	public Key getSignKey() {
		return Keys.hmacShaKeyFor(SECRETE_KEY.getBytes());
	}
	
	public String extractUserId(String token) {
		return extractClaim(token,claims -> claims.get("userId", String.class));
	}
	
	public String extractUserRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = extractAllClaims(token);
		return resolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
	            .setSigningKey(getSignKey())
	            .build()
	            .parseClaimsJws(token.replace("Bearer ", ""))
	            .getBody();
	}
	
	public boolean isTokenValid(String token, String expectedUserId, String expectedRole) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenUserId = claims.get("userId", String.class);
            String tokenRole = claims.get("role", String.class);
            Date expiration = claims.getExpiration();

            return expectedUserId.equals(tokenUserId)
                    && expectedRole.equals(tokenRole)
                    && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
		
	
	
	public String generateToken(String userId, String role) {
	    Map<String, Object> claims = new HashMap<>();
	    claims.put("userId", userId);
	    claims.put("role", role);
	    return generateToken(claims); // ✅ this uses your working method
	}
	
	public String generateToken(Map<String, Object> extraClaims) {
	    return Jwts.builder()
	        .setClaims(extraClaims)
	        .setIssuedAt(new Date(System.currentTimeMillis()))
	        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hrs
	        .signWith(getSignKey())
	        .compact();
	}
	
}
