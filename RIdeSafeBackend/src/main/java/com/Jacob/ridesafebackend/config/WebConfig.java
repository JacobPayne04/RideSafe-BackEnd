package com.Jacob.ridesafebackend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.Jacob.ridesafebackend.filters.MapsRateLimitFilter;
import com.Jacob.ridesafebackend.service.GoogleMapsRateLimiter;

@Configuration
@EnableScheduling
public class WebConfig implements WebMvcConfigurer {

    private final GoogleMapsRateLimiter rateLimiter;
    
    public WebConfig(GoogleMapsRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all paths
                .allowedOrigins("http://localhost:3000") // Your frontend URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Methods you want to allow
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // If you need to send cookies or credentials
    }

    @Bean
    public FilterRegistrationBean<MapsRateLimitFilter> rateLimitFilter() {
        FilterRegistrationBean<MapsRateLimitFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new MapsRateLimitFilter(rateLimiter)); // Pass the injected rateLimiter
        bean.addUrlPatterns("/{id}/MapRoute/*"); // only affects these routes
        bean.setOrder(1); // optional priority
        return bean;
    }
}