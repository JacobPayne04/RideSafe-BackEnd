package com.Jacob.ridesafebackend.dto;

public class DriverStatusCoordinatesRequest {

	
	private boolean isOnline;
	private Double longitude;
	private Double Latitude;
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return Latitude;
	}
	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}
	
	
	
	
}
