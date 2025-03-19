package com.Jacob.ridesafebackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.dto.PaymentRequest;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

@RestController
public class WebHookController {
	
	@Autowired
	private final PaymentRequest paymentRequest;
	
	public WebHookController(PaymentRequest paymentRequest) {
		this.paymentRequest = paymentRequest;
	}

	
	private final String endpointSecret = "whsex_YOUR_WEBHOOK_SECRET";

	@PostMapping("Stripe")
	public ResponseEntity<String> handleStripeWebHook(@RequestBody String payload,
			@RequestHeader("Stripe-Signiature") String sigHeader) {
		Event event;

		try {
			event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

		} catch (SignatureVerificationException e) {
			return ResponseEntity.badRequest().body("Invalid signature");
		}
			
		switch(event.getTyped()) {
		case "payment_intent.succeed":
				PaymentIntent paymentIntent = (paymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
				if(paymentIntent != null) {
					String paymentIntent = paymentRequest.getId();
					System.out.println("Payment succeeded for: " + paymentIntentId);
				}
		}
	}

}
