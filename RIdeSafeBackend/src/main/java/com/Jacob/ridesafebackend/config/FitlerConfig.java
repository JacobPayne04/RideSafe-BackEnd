package com.Jacob.ridesafebackend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.Jacob.ridesafebackend.filters.jwtAuthenticationFilter;

@Configuration
public class FitlerConfig {

	@Bean
	 public FilterRegistrationBean<jwtAuthenticationFilter> registerJwtFilter(jwtAuthenticationFilter filter) {
	        FilterRegistrationBean<jwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();

	        registrationBean.setFilter(filter);

	        // 🔐 Define which routes this filter protects
	        registrationBean.addUrlPatterns( 
	            "/edit/driver/*",
	            "/create-Payment-Intent",
	            "/refund",
	            "/update-ride-payment"
	        );

	        registrationBean.setOrder(1); // Optional: set filter order if you have others

	        return registrationBean;
	    }
	
	
	
}
