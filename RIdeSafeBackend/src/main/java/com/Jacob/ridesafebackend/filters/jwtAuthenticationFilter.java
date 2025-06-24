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

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        // 🔥 Extract user info first
        String userId = jwtService.extractUserId(token);
        String role = jwtService.extractUserRole(token);

        // 🔒 Validate the token's integrity and match claims
        if (!jwtService.isTokenValid(token, userId, role)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        // ✅ Optionally run deeper validation logic
        try {
            authValidationService.validateRequest(token, userId, role);
        } catch (UnauthorizedException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("User validation failed: " + e.getMessage());
            return;
        }

        // 🎯 Attach user info to request scope
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);

        // 🚀 Pass request onward
        chain.doFilter(request, response);
    }
	
}
