package com.Jacob.ridesafebackend.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

@RestController
public class WebHookController {
	
	
	

	
	private final String endpointSecret = "whsex_YOUR_WEBHOOK_SECRET";

	//#TODO fix this webhook controller to trigger the other requirements such as updating database and triggering webhook
	 @PostMapping("/stripe")
	    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
	                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
	        Event event;

	        try {
	            // Validate signature and construct the event
	            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
	        } catch (SignatureVerificationException e) {
	            // Invalid signature
	            return ResponseEntity.badRequest().body("Invalid signature");
	        }

	        // Handle the event type
	        switch (event.getType()) {
	            case "payment_intent.succeeded":
	                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
	                //TODO insert ride repo update to paid ride.
	                if (paymentIntent != null) {
	                    String paymentIntentId = paymentIntent.getId();
	                    System.out.println("Payment succeeded for: " + paymentIntentId);

	                    // TODO: Update your database (mark payment as complete, trigger ride, etc.)
	                }
	                break;

	            case "payment_intent.payment_failed":
	                PaymentIntent failedIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
	                if (failedIntent != null) {
	                    String failedIntentId = failedIntent.getId();
	                    System.out.println("Payment failed for: " + failedIntentId);

	                    // TODO: Handle the failure (send notification, update status)
	                }
	                break;

	            default:
	                System.out.println("Unhandled event type: " + event.getType());
	        }

	        return ResponseEntity.ok("");
	    }  

}
