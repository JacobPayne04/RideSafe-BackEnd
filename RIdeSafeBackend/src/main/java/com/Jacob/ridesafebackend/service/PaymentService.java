package com.Jacob.ridesafebackend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.dto.PaymentRequest;
import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.RideRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class PaymentService {
	
	private final RideRepository rideRepo;
	
	public PaymentService(RideRepository riderepo) {
		this.rideRepo = riderepo;
	}
		
	// Create payment intent
	public Map<String, String> createPaymentIntent(Ride ride) throws StripeException {
	    // Get the rate (cost of the ride) from the Ride object
	    int rideAmount = ride.getRate();  // The rate is the cost of the ride in dollars

	    // Convert the rideAmount (rate) from dollars to cents
	    long amountInCents = rideAmount * 100;  // Stripe expects the amount in cents

	    // Create the PaymentIntent with the total cost (in cents)
	    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
	            .setAmount(amountInCents)  // Use the ride amount in cents
	            .setCurrency("usd")  // Set the currency to USD
	            .build();

	    // Create the PaymentIntent using Stripe API
	    PaymentIntent paymentIntent = PaymentIntent.create(params);

	    // Prepare the response with the client secret for the frontend to use
	    Map<String, String> responseData = new HashMap<>();
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
