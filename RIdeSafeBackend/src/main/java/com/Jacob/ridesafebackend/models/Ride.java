package com.Jacob.ridesafebackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "Ride")
public class Ride {
	@Id
    private String id;
	
	public Ride(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
}
