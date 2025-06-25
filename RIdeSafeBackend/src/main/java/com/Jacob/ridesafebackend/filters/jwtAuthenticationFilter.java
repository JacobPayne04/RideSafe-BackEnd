package com.Jacob.ridesafebackend.filters;
import org.springframework.stereotype.Component;

import com.Jacob.ridesafebackend.exception.UnauthorizedException;
import com.Jacob.ridesafebackend.service.AuthValidationService;
import com.Jacob.ridesafebackend.service.jwtService;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class jwtAuthenticationFilter implements Filter {

	 private final jwtService jwtService;
	    private final AuthValidationService authValidationService;

	    public jwtAuthenticationFilter(jwtService jwtService, AuthValidationService authValidationService) {
	        this.jwtService = jwtService;
	        this.authValidationService = authValidationService;
	    }

	    @Override
	    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
	            throws IOException, ServletException {

	        HttpServletRequest request = (HttpServletRequest) servletRequest;
	        HttpServletResponse response = (HttpServletResponse) servletResponse;

	        // ✅ CORS Headers
	        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
	        response.setHeader("Access-Control-Allow-Credentials", "true");
	        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

	        // ✅ Skip JWT validation for OPTIONS (CORS preflight)
	        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
	            response.setStatus(HttpServletResponse.SC_OK);
	            return;
	        }

	        // 🔐 JWT Authentication
	        String authHeader = request.getHeader("Authorization");

	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.getWriter().write("Missing or invalid Authorization header");
	            return;
	        }

	        String token = authHeader.substring(7);

	        String userId = jwtService.extractUserId(token);
	        String role = jwtService.extractUserRole(token);

	        if (!jwtService.isTokenValid(token, userId, role)) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.getWriter().write("Invalid or expired token");
	            return;
	        }

	        try {
	            authValidationService.validateRequest(token, userId, role);
	        } catch (UnauthorizedException e) {
	            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	            response.getWriter().write("User validation failed: " + e.getMessage());
	            return;
	        }

	        request.setAttribute("userId", userId);
	        request.setAttribute("role", role);

	        chain.doFilter(request, response);
	    }
	
}
