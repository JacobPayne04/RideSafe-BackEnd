package com.Jacob.ridesafebackend.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Jacob.ridesafebackend.dto.PaymentRequest;
import com.Jacob.ridesafebackend.service.PaymentService;

@RestController
public class PaymentController {
	
	@Autowired
	private PaymentService paymentServ;
	
	@PostMapping("/create-Payment-Intent")
	public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
		
		int passengerCount = paymentRequest.getPassengerCount();
		int rate = paymentRequest.getRate();
		
		try {
			
			Map<String,String> clientSecretResponse = paymentServ.createPaymentIntent(paymentRequest);
			
		return ResponseEntity.ok(clientSecretResponse);
		
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(Map.of("error","Payment Intent Creation Failted"));
		}
		
	}
	
	//TODO payment sucess route to trigger webhook
	
	
}
