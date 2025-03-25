package com.Jacob.ridesafebackend.dto;

import org.springframework.data.annotation.Id;

public class PaymentRequest {
	
	private String paymentRequestRideId;
	
	@Id
	private int id;
	private int passengerCount;
	private int rate;

	
	public String getPaymentRequestRideId() {
		return paymentRequestRideId;
	}

	public void setPaymentRequestRideId(String paymentRequestRideId) {
		this.paymentRequestRideId = paymentRequestRideId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public int getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(int passengerCount) {
		this.passengerCount = passengerCount;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}
}
