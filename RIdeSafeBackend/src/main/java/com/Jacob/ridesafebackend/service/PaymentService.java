package com.Jacob.ridesafebackend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.dto.PaymentRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class PaymentService {
		
	
		//TODO left off fix where this service file usees teh get methods from payment dto or ride dto the logic is there
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

}
