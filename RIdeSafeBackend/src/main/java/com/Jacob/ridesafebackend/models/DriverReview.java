package com.Jacob.ridesafebackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "driver_reviews")
public class DriverReview {

	@Id
    private String id;

    private String driverId;  // Changed to String

    private int stars;

    public DriverReview() {}

    public DriverReview(String driverId, int stars) {
        this.driverId = driverId;
        this.stars = stars;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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
