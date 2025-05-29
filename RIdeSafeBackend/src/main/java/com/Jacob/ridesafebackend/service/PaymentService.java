package com.Jacob.ridesafebackend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.DriverRepository;
import com.Jacob.ridesafebackend.repositorys.RideRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

import jakarta.annotation.PostConstruct;


@Service
public class PaymentService {
	
	private final DriverRepository driverRepo;
	private final RideRepository rideRepo;
	private final SimpMessagingTemplate messagingTemplate;
	private final RideService rideServ;
	
	
	
	@Value("${stripe.secret.key}")
	private String stripeSecretKey;
	

	public PaymentService(RideRepository riderepo, SimpMessagingTemplate messagingTemplate, RideService rideServ, DriverRepository driverrepo) {
		this.driverRepo = driverrepo;
		this.rideRepo = riderepo;
		this.messagingTemplate = messagingTemplate;
		this.rideServ = rideServ;
	}
		
	 @PostConstruct
	    public void init() {
	        Stripe.apiKey = stripeSecretKey;
	    }
	 /**
	  * Creates a Stripe PaymentIntent for a specific ride.
	  * Converts the ride's rate to cents and returns client secret + intent ID.
	  */
	 public Map<String, String> createPaymentIntent(Ride ride) throws StripeException {
	 	int rideAmount = ride.getRate();
	 	long amountInCents = rideAmount * 100;

	 	PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
	 			.setAmount(amountInCents)
	 			.setCurrency("usd")
	 			.build();

	 	PaymentIntent paymentIntent = PaymentIntent.create(params);

	 	System.out.println("PaymentIntent ID: " + paymentIntent.getId());
	 	System.out.println("Client Secret: " + paymentIntent.getClientSecret());

	 	Map<String, String> responseData = new HashMap<>();
	 	responseData.put("clientSecret", paymentIntent.getClientSecret());
	 	responseData.put("paymentIntentId", paymentIntent.getId());

	 	return responseData;
	 }


	 /**
	  * Starts Stripe Express on boarding for a driver using their email.
	  * Saves the Stripe Account ID to the driver and returns the on boarding URL.
	  */
	 public String onboardDriver(String email, String driverId) throws Exception {
	 	AccountCreateParams accountParams = AccountCreateParams.builder()
	 			.setType(AccountCreateParams.Type.EXPRESS)
	 			.setEmail(email)
	 			.setCountry("US")
	 			.build();

	 	Account account = Account.create(accountParams);

	 	Optional<Driver> optionaldriver = driverRepo.findById(driverId);

	 	if (!optionaldriver.isPresent()) {
	 		throw new RuntimeException("Driver not found with driver Id: " + driverId);
	 	}

	 	Driver driver = optionaldriver.get();
	 	driver.setStripeAccountId(account.getId());
	 	driverRepo.save(driver);

	 	AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
	 			.setAccount(driver.getId())
	 			.setRefreshUrl("http://localhost:3000/driver/" + driverId + "/verification/account/setup")      // TODO: Update this for production
	 			.setReturnUrl("http://localhost:3000/driver/" + driverId + "/verification/account/setup?stripe=success")  // TODO: Update this for production
	 			.setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
	 			.build();

	 	AccountLink accountLink = AccountLink.create(linkParams);
	 	return accountLink.getUrl();
	 }


	 /**
	  * Marks a ride as paid and notifies the driver via WebSocket.
	  * Returns the updated ride if successful.
	  */
	 public Optional<Ride> updateRidePaymentAmount(String rideId) {
	 	try {
	 		Optional<Ride> rideOptional = rideRepo.findById(rideId);

	 		if (rideOptional.isEmpty()) {
	 			System.out.println("Ride not found: " + rideId);
	 			return Optional.empty();
	 		}

	 		Ride ride = rideOptional.get();

	 		if (ride.isPaid()) {
	 			System.out.println("Ride is already marked as paid: " + rideId);
	 			return Optional.empty();
	 		}

	 		ride.setPaid(true);
	 		rideRepo.save(ride);

	 		// Notify driver that the ride has been paid
	 		Optional<String> driverIdOptional = rideServ.getDriverIdByRideId(rideId);
	 		driverIdOptional.ifPresent(driverId -> {
	 			String driverDestination = "/topic/driver/" + driverId;

	 			Map<String, Object> notification = new HashMap<>();
	 			notification.put("title", "Ride Payment Received");
	 			notification.put("message", "A passenger has paid for the ride.");
	 			notification.put("rideId", ride.getId());

	 			messagingTemplate.convertAndSend(driverDestination, notification);
	 			System.out.println("Sent WebSocket message to driver: " + driverId);
	 		});

	 		return Optional.of(ride);

	 	} catch (Exception e) {
	 		System.err.println("Error updating ride payment: " + e.getMessage());
	 		e.printStackTrace();
	 		return Optional.empty();
	 	}
	 }


	 /**
	  * Processes a Stripe refund for a paid ride using the stored PaymentIntent ID.
	  * Updates local DB to reflect refunded status.
	  */
	 public Optional<Ride> refundRide(String rideId) {
	 	try {
	 		Optional<Ride> rideOptional = rideRepo.findById(rideId);

	 		if (rideOptional.isEmpty()) {
	 			System.out.println("Ride not found: " + rideId);
	 			return Optional.empty();
	 		}

	 		Ride ride = rideOptional.get();

	 		if (!ride.isPaid()) {
	 			System.out.println("Ride is not paid: " + rideId);
	 			return Optional.empty();
	 		}

	 		String paymentIntentId = ride.getPaymentRequestRideId(); // Must be saved in your DB when creating intent

	 		if (paymentIntentId == null || paymentIntentId.isEmpty()) {
	 			System.out.println("PaymentIntent ID is missing for ride: " + rideId);
	 			return Optional.empty();
	 		}

	 		// Issue refund through Stripe
	 		RefundCreateParams params = RefundCreateParams.builder()
	 				.setPaymentIntent(paymentIntentId)
	 				.build();

	 		Refund refund = Refund.create(params);
	 		System.out.println("Refund issued: " + refund.getId());

	 		// Update ride record
	 		ride.setPaid(false);
	 		ride.setRefunded(true);  // Optional: useful for auditing or UI flags
	 		rideRepo.save(ride);

	 		return Optional.of(ride);

	 	} catch (StripeException e) {
	 		System.err.println("Stripe error during refund: " + e.getMessage());
	 		e.printStackTrace();
	 		return Optional.empty();

	 	} catch (Exception e) {
	 		System.err.println("Error processing refund: " + e.getMessage());
	 		e.printStackTrace();
	 		return Optional.empty();
	 	}
	 }
	 
	 
}
