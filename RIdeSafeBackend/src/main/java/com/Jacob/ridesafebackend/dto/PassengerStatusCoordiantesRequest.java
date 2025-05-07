package com.Jacob.ridesafebackend.dto;

public class PassengerStatusCoordiantesRequest {

    private boolean isOnline;                      // ✅ correct field name
    private Double longitude;
    private Double latitude;                       // ✅ lowercase

    public boolean isOnline() {                    // ✅ getter
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {    // ✅ MUST match field name for JSON binding
        this.isOnline = isOnline;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
	
}
