package com.Jacob.ridesafebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // Disable CSRF (unless you need it)
            .cors().disable()  // Disable CORS (you might need it if using cross-origin requests)
            .authorizeRequests()
                .anyRequest().permitAll();  // Allow all requests (adjust as needed)

        return http.build();
    }
}
