package com.Jacob.ridesafebackend.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
@Document(collection = "Ride")
public class Ride {
	@Id
    private String id;
	
	private String passengerId;
	private String driverId;
	
	private String fromLocation;
	private String toLocation;
	
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    public enum RideStatus {
        PENDING,
        COMPLETED,
        CANCELED
    }

	
	private LocalDateTime createdAt;
	
	
	
	public String getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(String passengerId) {
		this.passengerId = passengerId;
	}

	
	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	public RideStatus getStatus() {
		return status;
	}

	public void setStatus(RideStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Ride(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
}
