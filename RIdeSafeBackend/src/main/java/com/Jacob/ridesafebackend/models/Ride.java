package com.Jacob.ridesafebackend.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "Ride")
public class Ride {
	@Id
    private String id;
	
	private String passengerId;
	private String DriverId;
	
	private String fromLocation;
	private String toLocation;
	private String status;
	
	public void setStatus(String status) {
		this.status = status;
	}


	
	public String getStatus() {
		return status;
	}

	private LocalDateTime createdAt;
	
	
	
	public String getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(String passengerId) {
		this.passengerId = passengerId;
	}

	public String getDriverId() {
		return DriverId;
	}

	public void setDriverId(String driverId) {
		DriverId = driverId;
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
