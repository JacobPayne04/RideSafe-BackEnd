package com.Jacob.ridesafebackend.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.service.PaymentService;
import com.google.api.client.util.Value;
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
	    

	    @Value("${stripe.webhook.secret:${STRIPE_WEBHOOK_SECRET}}")
	    private String endpointSecret;
	
	    @PostMapping("/stripe/paymentStatus")
	    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
	                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
	    	  System.out.println("🚨 Stripe Webhook Secret at runtime: " + endpointSecret);
	        System.out.println("🔔 Webhook endpoint was hit");
	        System.out.println("Received Stripe-Signature: " + sigHeader);

	        Event event;

	        try {
	            System.out.println("🔐 Validating signature using secret: " + endpointSecret);
	            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
	            System.out.println("✅ Webhook signature verified. Event type: " + event.getType());
	        } catch (SignatureVerificationException e) {
	            System.out.println("❌ Invalid signature: " + e.getMessage());
	            return ResponseEntity.badRequest().body("Invalid signature");
	        }

	        switch (event.getType()) {
	            case "payment_intent.succeeded":
	                System.out.println("💳 Handling payment success");

	                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
	                        .getObject()
	                        .orElse(null);

	                if (paymentIntent != null) {
	                    String paymentIntentId = paymentIntent.getId();
	                    System.out.println("📦 PaymentIntent ID: " + paymentIntentId);

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

	//#TODO fix this webhook controller to trigger the other requirements such as updating database and triggering webhook
//	 @PostMapping("/stripe/paymentStatus")
//	    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
//	                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
//	        Event event;
//
//	        try {
//	            // Validate signature and construct the event
//	            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
//	        } catch (SignatureVerificationException e) {
//	            // Invalid signature
//	            return ResponseEntity.badRequest().body("Invalid signature");
//	        }
//
//	        // Handle the event type
//	        switch (event.getType()) {
//	            case "payment_intent.succeeded":
//	                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
//	                //TODO insert ride repo update to paid ride.
//	                if (paymentIntent != null) {
//	                    String paymentIntentId = paymentIntent.getId();
//	                    System.out.println("Payment succeeded for: " + paymentIntentId);
//
//	                    // TODO: Update your database (mark payment as complete, trigger ride, etc.)
//	                }
//	                break;
//
//	            case "payment_intent.payment_failed":
//	                PaymentIntent failedIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
//	                if (failedIntent != null) {
//	                    String failedIntentId = failedIntent.getId();
//	                    System.out.println("Payment failed for: " + failedIntentId);
//
//	                    // TODO: Handle the failure (send notification, update status)
//	                }
//	                break;
//
//	            default:
//	                System.out.println("Unhandled event type: " + event.getType());
//	        }
//
//	        return ResponseEntity.ok("");
//	    }  

}
