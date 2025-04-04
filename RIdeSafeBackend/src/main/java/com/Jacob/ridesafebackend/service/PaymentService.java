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
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

import jakarta.annotation.PostConstruct;


@Service
public class PaymentService {
	
	
	private final RideRepository rideRepo;
	private final SimpMessagingTemplate messagingTemplate;
	private final RideService rideServ;
	
	
	
	@Value("${stripe.secret.key}")
	private String stripeSecretKey;
	

	public PaymentService(RideRepository riderepo, SimpMessagingTemplate messagingTemplate, RideService rideServ) {
		this.rideRepo = riderepo;
		this.messagingTemplate = messagingTemplate;
		this.rideServ = rideServ;
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


	 public Optional<Ride> updateRidePaymentAmount(String rideId) {
		    try {
		        Optional<Ride> rideOptional = rideRepo.findById(rideId);

		        if (rideOptional.isEmpty()) {
		            System.out.println("Ride not found: " + rideId);
		            return Optional.empty();
		        }

		        Ride ride = rideOptional.get();

		        if (ride.isPaid()) {
		            System.out.println("Ride is already marked as paid: " + rideId);
		            return Optional.empty();
		        }

		        ride.setPaid(true);
		        rideRepo.save(ride);

		        Optional<String> driverIdOptional = rideServ.getDriverIdByRideId(rideId);
		        driverIdOptional.ifPresent(driverId -> {
		            String driverDestination = "/topic/driver/" + driverId;
		            Map<String, Object> notification = new HashMap<>();
		            notification.put("title", "Ride Payment Received");
		            notification.put("message", "A passenger has paid for the ride.");
		            notification.put("rideId", ride.getId());

		            messagingTemplate.convertAndSend(driverDestination, notification);
		            System.out.println("Sent WebSocket message to driver: " + driverId);
		        });

		        return Optional.of(ride);
		    } catch (Exception e) {
		        System.err.println("Error updating ride payment: " + e.getMessage());
		        e.printStackTrace();
		        return Optional.empty();
		    }
		}
	 
	 
	 public Optional<Ride> refundRide(String rideId) {
		    try {
		        Optional<Ride> rideOptional = rideRepo.findById(rideId);

		        if (rideOptional.isEmpty()) {
		            System.out.println("Ride not found: " + rideId);
		            return Optional.empty();
		        }

		        Ride ride = rideOptional.get();

		        if (!ride.isPaid()) {
		            System.out.println("Ride is not paid: " + rideId);
		            return Optional.empty();
		        }

		        String paymentIntentId = ride.getPaymentRequestRideId(); // Make sure you store this in your Ride model

		        if (paymentIntentId == null || paymentIntentId.isEmpty()) {
		            System.out.println("PaymentIntent ID is missing for ride: " + rideId);
		            return Optional.empty();
		        }

		        // Step 1: Refund via Stripe
		        RefundCreateParams params = RefundCreateParams.builder()
		            .setPaymentIntent(paymentIntentId)
		            .build();

		        Refund refund = Refund.create(params);
		        System.out.println("Refund issued: " + refund.getId());

		        // Step 2: Update your app DB
		        ride.setPaid(false);
		        ride.setRefunded(true); // Optional: add this field if useful
		        rideRepo.save(ride);

		        return Optional.of(ride);

		    } catch (StripeException e) {
		        System.err.println("Stripe error during refund: " + e.getMessage());
		        e.printStackTrace();
		        return Optional.empty();
		    } catch (Exception e) {
		        System.err.println("Error processing refund: " + e.getMessage());
		        e.printStackTrace();
		        return Optional.empty();
		    }
		}
	 
	 
}
