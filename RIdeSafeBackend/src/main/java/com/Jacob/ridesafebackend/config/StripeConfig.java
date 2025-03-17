package com.Jacob.ridesafebackend.config;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

public class StripeConfig {
	
@PostConstruct
public void inIt() {
	 Stripe.apiKey = "sk_test_YOUR_SECRET_KEY";//change this to actual key later
}

}
