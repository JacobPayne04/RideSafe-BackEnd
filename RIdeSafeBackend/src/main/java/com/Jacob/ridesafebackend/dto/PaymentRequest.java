package com.Jacob.ridesafebackend.dto;

import org.springframework.data.annotation.Id;

public class PaymentRequest {
	@Id
	private int id;
	private int passengerCount;
	private int rate;

	
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
