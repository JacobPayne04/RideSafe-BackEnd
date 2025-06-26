package com.Jacob.ridesafebackend.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.*;


@Service
public class GoogleMapsRateLimiter {

	   
    private final Map<String, List<Long>> requestTimestamps = new ConcurrentHashMap<>();
    private final int maxRequests = 5;
    private final long timeWindowMs = 60000; // 1 minute
    
    public boolean canMakeRequest(String clientIP) {
        long now = System.currentTimeMillis();
        
        // Get or create request list for this IP
        List<Long> requests = requestTimestamps.computeIfAbsent(clientIP, k -> new ArrayList<>());
        
        // Remove old requests (older than 1 minute)
        requests.removeIf(timestamp -> now - timestamp > timeWindowMs);
        
        // Check if under limit
        if (requests.size() < maxRequests) {
            requests.add(now);
            return true;
        }
        
        return false;
    }
    
    // Clean up old entries periodically
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void cleanup() {
        long now = System.currentTimeMillis();
        requestTimestamps.entrySet().removeIf(entry -> {
            entry.getValue().removeIf(timestamp -> now - timestamp > timeWindowMs);
            return entry.getValue().isEmpty();
        });
    }
}
