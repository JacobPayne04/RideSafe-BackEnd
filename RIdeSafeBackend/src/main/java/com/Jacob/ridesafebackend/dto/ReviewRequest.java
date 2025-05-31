package com.Jacob.ridesafebackend.dto;

public class ReviewRequest {
    private String driverId;
    private int stars;

    public ReviewRequest() {}

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}