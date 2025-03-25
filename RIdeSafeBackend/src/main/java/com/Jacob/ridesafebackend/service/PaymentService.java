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
		
	
		//TODO add payment id into the payload IF needed
	  public Map<String, String> createPaymentIntent(PaymentRequest paymentRequest) throws StripeException {
	        int passengerCount = paymentRequest.getPassengerCount();
	        int rate = paymentRequest.getRate();

	        // Secure the rate in backend (optional)
	        // int rate = fetchRateForDriver(paymentRequest.getDriverId());

	        // Calculate total in cents
	        int amount = passengerCount * rate * 100;

	        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
	                .setAmount((long) amount)
	                .setCurrency("usd") // or another currency
	                .build();

	        PaymentIntent paymentIntent = PaymentIntent.create(params);

	        Map<String, String> responseData = new HashMap<>();
	        responseData.put("clientSecret", paymentIntent.getClientSecret());

	        return responseData;
	    }		
	  
	  
	  //#TODO service method for updating boolean status of ride to paid;
	 
	  public Optional<Ride> updateRidePaymentStatus(String ridePaymentId) {

		    Optional<Ride> optionalRide = rideRepo.findRideByIdAndPaid(ridePaymentId, false);

		    if (optionalRide.isEmpty()) {
		        // No unpaid ride found with this id
		        return Optional.empty();
		    }

		    Ride ride = optionalRide.get();
		    ride.setPaid(true);
		    rideRepo.save(ride);

		    return Optional.of(ride);
		}

}
