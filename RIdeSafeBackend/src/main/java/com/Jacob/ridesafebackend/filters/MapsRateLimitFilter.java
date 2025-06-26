package com.Jacob.ridesafebackend.filters;
import java.io.IOException;


import org.springframework.stereotype.Component;

import com.Jacob.ridesafebackend.service.GoogleMapsRateLimiter;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MapsRateLimitFilter implements Filter {

	  private final GoogleMapsRateLimiter rateLimiter;
	    
	    public MapsRateLimitFilter(GoogleMapsRateLimiter rateLimiter) {
	        this.rateLimiter = rateLimiter;
	    }
	    
	    @Override
	    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	            throws IOException, ServletException {
	        
	        HttpServletRequest httpRequest = (HttpServletRequest) request;
	        HttpServletResponse httpResponse = (HttpServletResponse) response;
	        
	        // Only check Google Maps API calls
	        String uri = httpRequest.getRequestURI();
	        if (uri.contains("/maps") || uri.contains("/geocode")) {
	            
	            String clientIP = getClientIP(httpRequest);
	            
	            if (!rateLimiter.canMakeRequest(clientIP)) {
	                httpResponse.setStatus(429); // Too Many Requests
	                httpResponse.setContentType("application/json");
	                httpResponse.getWriter().write(
	                    "{\"error\":\"Too many requests\",\"message\":\"Maximum 5 requests per minute allowed\"}"
	                );
	                return;
	            }
	        }
	        
	        chain.doFilter(request, response);
	    }
	    
	    private String getClientIP(HttpServletRequest request) {
	        String ip = request.getHeader("X-Forwarded-For");
	        if (ip == null || ip.isEmpty()) {
	            ip = request.getRemoteAddr();
	        }
	        return ip;
	    }

}
