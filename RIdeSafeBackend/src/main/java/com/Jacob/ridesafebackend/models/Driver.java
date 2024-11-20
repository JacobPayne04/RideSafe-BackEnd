package com.Jacob.ridesafebackend.models;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Document(collection = "drivers")
public class Driver {

	@Id
	private String id;
	
	@NotEmpty(message = "First name is required!")
	@Size(min = 3,max = 15,message = "First name must be between 3 and 15 characters")
	private String firstName;
	
	@NotEmpty(message = "Last name is required!")
	@Size(min = 3,max = 15,message = "Last name must be between 3 and 15 characters")
	private String lastName;
	
	@NotEmpty(message = "Email is required!")
	@Email(message = "Please enter a valid email!")
	 private String email;
	
	@NotEmpty(message = "Password is required!")
	@Size(min = 8,max = 128,message = "Password must be between 8 and 200 characters" )
	private String password;
	
	@Transient
	@NotEmpty(message = "ConfirmPassword is required!")
	@Size(min = 8,max = 128,message = "Confirm Password must be between 8 and 200 characters" )
	private String confirm;
	
	@NotEmpty(message = "License plate is required!")
	@Size(min = 5,max = 9,message = "License plate must be between 3 and 9 characters" )
	private String licensePlate;
	
	

	//What we call in the routes
	public Driver() {}

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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	
	
	}
//Driver model