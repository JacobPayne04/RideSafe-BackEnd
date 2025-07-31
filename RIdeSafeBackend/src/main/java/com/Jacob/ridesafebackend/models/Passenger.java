package com.Jacob.ridesafebackend.models;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Document(collection = "passenger")
public class Passenger {
	
	@Id
    private String id;
	
	@CreatedDate
	private LocalDateTime createdAt;	
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
	private GeoJsonPoint location;
    
    @NotEmpty(message = "First name is required!")
    @Size(min = 3,max = 15,message = "First name must be between 3 and 15 characters")
    private String firstName;
    
    @NotEmpty(message = "Last name is required!")
    @Size(min = 3,max = 15,message = "Last name must be between 3 and 15 characters")
    private String lastName;
    
    @NotEmpty(message = "Last name is required!")
    @Size(min = 3,max = 15,message = "Last name must be between 3 and 15 characters")
    private String VenmoUserName;
    
    @NotEmpty(message = "Last name is required!")
    @Size(min = 3,max = 15,message = "Last name must be between 3 and 15 characters")
    private String CashAppUserName;
    
    @NotEmpty(message = "Email is required!")
	@Email(message = "Please enter a valid email!")
	 private String email;
    
    @NotEmpty(message = "Password is required!")
    @Size(min = 8,max = 128,message = "Password must be between 8 and 200 characters" )
    private String password;
    
    @NotEmpty(message = "ConfirmPassword is required!")
    @Size(min = 8,max = 128,message = "Confirm Password must be between 8 and 200 characters" )
    private String confirm;
    
    private String googleId; 
    
    
    public GeoJsonPoint getLocation() {
		return location;
	}

	public void setLocation(GeoJsonPoint location) {
		this.location = location;
	}

	public String getVenmoUserName() {
		return VenmoUserName;
	}

	public void setVenmoUserName(String venmoUserName) {
		VenmoUserName = venmoUserName;
	}

	public String getCashAppUserName() {
		return CashAppUserName;
	}

	public void setCashAppUserName(String cashAppUserName) {
		CashAppUserName = cashAppUserName;
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public Passenger() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
    
    
}
