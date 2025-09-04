package com.Jacob.ridesafebackend.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.service.PaymentService;

import org.springframework.beans.factory.annotation.Value;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

@RestController
public class WebHookController {
	
		private final PaymentService paymentService;
		

	    public WebHookController(PaymentService paymentService) {
	        this.paymentService = paymentService;
	    }
	    

	    @Value("${stripe.webhook.secret}")
	    private String endpointSecret;

	
	    /**
	     * Handles incoming Stripe web hook events.
	     * Validates the signature and processes payment-related events like success and failure.
	     */
	    @PostMapping("/api/v1/webhooks/stripe/payment-status")
	    public ResponseEntity<String> handleStripeWebhook(
	    		@RequestBody String payload,
	    		@RequestHeader("Stripe-Signature") String sigHeader) {

	    	System.out.println("🚨 Stripe Webhook Secret at runtime: " + endpointSecret);
	    	System.out.println("🔔 Webhook endpoint was hit");
	    	System.out.println("Received Stripe-Signature: " + sigHeader);

	    	Event event;

	    	try {
	    		// Validate Stripe signature using the endpoint secret
	    		System.out.println("🔐 Validating signature using secret: " + endpointSecret);
	    		event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
	    		System.out.println("✅ Webhook signature verified. Event type: " + event.getType());

	    	} catch (SignatureVerificationException e) {
	    		System.out.println("❌ Invalid signature: " + e.getMessage());
	    		return ResponseEntity.badRequest().body("Invalid signature");
	    	}

	    	// Handle the specific type of Stripe event
	    	switch (event.getType()) {

	    		case "payment_intent.succeeded":
	    			System.out.println("💳 Handling payment success");

	    			// Deserialize payment intent object
	    			PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
	    					.getObject()
	    					.orElse(null);

	    			if (paymentIntent != null) {
	    				String paymentIntentId = paymentIntent.getId();
	    				System.out.println("📦 PaymentIntent ID: " + paymentIntentId);

	    				// #TODO: In the future, fetch driverId from metadata if needed
	    				String rideId = paymentIntent.getMetadata().get("rideId");
	    				System.out.println("🛻 rideId from metadata: " + rideId);

	    				if (rideId != null) {
	    					paymentService.updateRidePaymentAmount(rideId)
	    							.ifPresentOrElse(
	    									ride -> System.out.println("✅ Ride " + ride.getId() + " marked as PAID."),
	    									() -> System.out.println("⚠️ No unpaid ride found for rideId: " + rideId)
	    							);
	    				} else {
	    					System.out.println("❗ rideId not found in metadata");
	    				}

	    			} else {
	    				System.out.println("⚠️ PaymentIntent object was null");
	    			}
	    			break;

	    		case "payment_intent.payment_failed":
	    			System.out.println("❌ Handling failed payment");

	    			PaymentIntent failedIntent = (PaymentIntent) event.getDataObjectDeserializer()
	    					.getObject()
	    					.orElse(null);

	    			if (failedIntent != null) {
	    				System.out.println("❌ Payment failed for: " + failedIntent.getId());
	    			}
	    			break;

	    		default:
	    			System.out.println("🤷 Unhandled event type: " + event.getType());
	    	}

	    	return ResponseEntity.ok("Webhook processed");
	    }
	

}
