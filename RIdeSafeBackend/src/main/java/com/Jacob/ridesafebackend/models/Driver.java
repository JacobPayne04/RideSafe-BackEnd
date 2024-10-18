package com.Jacob.ridesafebackend.models;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
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
	
	
	private List<String> driverIds;
	
	public Driver() {}
	}
//Driver model