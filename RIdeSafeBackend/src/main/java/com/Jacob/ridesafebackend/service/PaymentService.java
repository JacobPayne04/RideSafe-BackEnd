package com.Jacob.ridesafebackend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
	private final SimpMessagingTemplate messagingTemplate;
	
	private final RideRepository rideRepo;
	
	
	
	@Value("${stripe.secret.key}")
	private String stripeSecretKey;
	
	public PaymentService(RideRepository riderepo,SimpMessagingTemplate messagingTemplate) {
		this.rideRepo = riderepo;
		this.messagingTemplate = messagingTemplate;
	}
		
	 @PostConstruct
	    public void init() {
	        Stripe.apiKey = stripeSecretKey;
	    }
	// Create payment intent
	 public Map<String, String> createPaymentIntent(Ride ride) throws StripeException {
		    int rideAmount = ride.getRate();
		    long amountInCents = rideAmount * 100;

		    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
		            .setAmount(amountInCents)
		            .setCurrency("usd")
		            .build();

		    PaymentIntent paymentIntent = PaymentIntent.create(params);

		    // Log the PaymentIntent ID and clientSecret before returning
		    System.out.println("PaymentIntent ID: " + paymentIntent.getId());
		    System.out.println("Client Secret: " + paymentIntent.getClientSecret());

		    Map<String, String> responseData = new HashMap<>();
		    responseData.put("clientSecret", paymentIntent.getClientSecret());
		    responseData.put("paymentIntentId", paymentIntent.getId());

		    return responseData;
		}


		

	// Update ride payment status
	public Optional<Ride> updateRidePaymentAmount(String ridePaymentId) {
	    return rideRepo.findRideByIdAndPaid(ridePaymentId, false).map(ride -> {
	        ride.setPaid(true);
	        rideRepo.save(ride);
	        String driverDestination = "/topic/driver/" + ride.getDriverId(); // WebSocket destination
	        Map<String, Object> notification = new HashMap<>();
	        notification.put("title", "New Ride Request");
	        notification.put("message", "A new passenger has booked a ride.");
	        notification.put("rideId", ride.getId());
	        messagingTemplate.convertAndSend(driverDestination, notification);
	        System.out.println(ride.getId() +  " " + ride.getDriverId()+ " ");
	        return ride;
	    });
	}
}
