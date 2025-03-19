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
	private double passengerAmount;
	private boolean isPaid;
	
    // From Location (Pickup)
    private String fromLocation;
    private double fromLatitude;
    private double fromLongitude;
    
    
    public double getPassengerAmount() {
		return passengerAmount;
	}

	public void setPassengerAmount(double passengerAmount) {
		this.passengerAmount = passengerAmount;
	}

	public double getFromLatitude() {
		return fromLatitude;
	}

	public void setFromLatitude(double fromLatitude) {
		this.fromLatitude = fromLatitude;
	}

	public double getFromLongitude() {
		return fromLongitude;
	}

	public void setFromLongitude(double fromLongitude) {
		this.fromLongitude = fromLongitude;
	}

	public double getToLatitude() {
		return toLatitude;
	}

	public void setToLatitude(double toLatitude) {
		this.toLatitude = toLatitude;
	}

	public double getToLongitude() {
		return toLongitude;
	}

	public void setToLongitude(double toLongitude) {
		this.toLongitude = toLongitude;
	}

	public void setStatus(RideStatus status) {
		this.status = status;
	}

	// To Location (Destination)
    private String toLocation;
    private double toLatitude;
    private double toLongitude;
    private int queuePosition;

    public int getQueuePosition() {
		return queuePosition;
	}

	public void setQueuePosition(int queuePosition) {
		this.queuePosition = queuePosition;
	}

	@Enumerated(EnumType.STRING)
    private RideStatus status;

    public enum RideStatus {
        PENDING,
        COMPLETED,
        CANCELED,
        ONGOING,
        INQUEUE
    }


	private LocalDateTime createdAt;
	
	
	public RideStatus getStatus() {
		return status;
	}
	
	public void setRideStatus(RideStatus status) {
		this.status = status;
	}
	
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