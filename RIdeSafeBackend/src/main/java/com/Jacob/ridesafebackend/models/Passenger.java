package com.Jacob.ridesafebackend.models;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "passenger")
public class Passenger {
	
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
    
    @NotEmpty(message = "ConfirmPassword is required!")
    @Size(min = 8,max = 128,message = "Confirm Password must be between 8 and 200 characters" )
    private String confirm;

}
