package com.Jacob.ridesafebackend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.RideRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;


@Service
public class PaymentService {
	
	private final RideRepository rideRepo;
	
	
	
	@Value("${stripe.secret.key}")
	private String stripeSecretKey;
	
	public PaymentService(RideRepository riderepo) {
		this.rideRepo = riderepo;
	}
		
	 @PostConstruct
	    public void init() {
	        Stripe.apiKey = stripeSecretKey;
	    }
	// Create payment intent
	public Map<String, String> createPaymentIntent(Ride ride) throws StripeException {
		  
		
	    // Get the rate (cost of the ride) from the Ride object
	    int rideAmount = ride.getRate();  // The rate is the cost of the ride in dollars

	    // Convert the rideAmounat (rate) from dollars to cents
	    long amountInCents = rideAmount * 100;  // Stripe expects the amount in cents
	    
	    System.out.print("Creating Payment with amount: " + amountInCents);

	    // Create the PaymentIntent with the total cost (in cents)
	    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
	            .setAmount(amountInCents)  // Use the ride amount in cents
	            .setCurrency("usd")  // Set the currency to USD
	            .build();

	    // Create the PaymentIntent using Stripe API
	    PaymentIntent paymentIntent = PaymentIntent.create(params);

	    // Prepare the response with the client secret for the frontend to use
	    Map<String, String> responseData = new HashMap<>();
	    System.out.println("Payment Intent created with client secret: " + paymentIntent.getClientSecret());  // Add this line
	    responseData.put("clientSecret", paymentIntent.getClientSecret());

	    return responseData;
	}
		

	// Update ride payment status
	public Optional<Ride> updateRidePaymentAmount(String ridePaymentId) {
	    return rideRepo.findRideByIdAndPaid(ridePaymentId, false).map(ride -> {
	        ride.setPaid(true);
	        rideRepo.save(ride);
	        return ride;
	    });
	}
}
