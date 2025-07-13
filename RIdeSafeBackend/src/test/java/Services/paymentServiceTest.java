package Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.DriverRepository;
import com.Jacob.ridesafebackend.repositorys.RideRepository;
import com.Jacob.ridesafebackend.service.DriverService;
import com.Jacob.ridesafebackend.service.PaymentService;
import com.Jacob.ridesafebackend.service.RideService;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

@ExtendWith(MockitoExtension.class)
public class paymentServiceTest {
	
	@Mock
	private RideRepository rideRepo;
	
	@Mock
	private DriverRepository driverRepo;
	
	@Mock
	private SimpMessagingTemplate messagingTemplate;
	
	@InjectMocks
	private DriverService driverServ;
	
	@InjectMocks
	private RideService rideServ;
	
	@InjectMocks
	private PaymentService paymentServ;
	


	@Test
	void createPaymentIntent_shouldReturnResponseData() throws StripeException {
	    // Arrange
	    Ride ride = new Ride();
	    ride.setRate(2); // $2
	    long expectedAmountInCents = 200;

	    // Mock the PaymentIntent that Stripe would return
	    PaymentIntent mockIntent = mock(PaymentIntent.class);
	    when(mockIntent.getId()).thenReturn("pi_123");
	    when(mockIntent.getClientSecret()).thenReturn("secret_abc");

	    try (MockedStatic<PaymentIntent> paymentIntentStatic = mockStatic(PaymentIntent.class)) {
	        // Mock the static Stripe method
	        paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
	            .thenReturn(mockIntent);

	        // Act
	        Map<String, String> result = paymentServ.createPaymentIntent(ride);

	        // Assert result map contains expected values
	        assertEquals("secret_abc", result.get("clientSecret"));
	        assertEquals("pi_123", result.get("paymentIntentId"));

	        // Verify that Stripe.create was called with the correct PaymentIntentCreateParams
	        paymentIntentStatic.verify(() ->
	            PaymentIntent.create(argThat((PaymentIntentCreateParams params) ->
	                params.getAmount() == expectedAmountInCents &&
	                "usd".equals(params.getCurrency())
	            )),
	            times(1)
	        );
	    }
	}
	
	
	
	
	@Test
	void onboardDriver_ShouldConnectDriverAccountToStripeAccount() throws Exception {
	    // Arrange
	    String driverEmail = "driver@123.com";
	    String driverId = "123";
	    
	    Driver driver = new Driver();
	    driver.setId(driverId);
	    
	    when(driverRepo.findById(driverId)).thenReturn(Optional.of(driver));

	    Account mockAccount = mock(Account.class);
	    when(mockAccount.getId()).thenReturn("acct_abc");

	    AccountLink mockAccountLink = mock(AccountLink.class);
	    when(mockAccountLink.getUrl()).thenReturn("https://stripe.com/onboard");

	    try (
	        MockedStatic<Account> accountStatic = mockStatic(Account.class);
	        MockedStatic<AccountLink> linkStatic = mockStatic(AccountLink.class)
	    ) {
	        accountStatic.when(() -> Account.create(any(AccountCreateParams.class)))
	                     .thenReturn(mockAccount);

	        linkStatic.when(() -> AccountLink.create(any(AccountLinkCreateParams.class)))
	                  .thenReturn(mockAccountLink);

	        // Act
	        String resultUrl = paymentServ.onboardDriver(driverEmail, driverId);

	        // Assert
	        assertEquals("https://stripe.com/onboard", resultUrl);
	        verify(driverRepo).save(argThat(d -> "acct_abc".equals(d.getStripeAccountId())));
	    }
	}

	
	@Test
	void updateRidePaymentAmount_shouldMarkRideAndSendNotification() {
		Ride unpaidRide = new Ride();
		unpaidRide.setId("123");
		unpaidRide.setPaid(false);
		
		when(rideRepo.findById("123")).thenReturn(Optional.of(unpaidRide));
		
		when(rideServ.getDriverIdByRideId("123")).thenReturn(Optional.of("driver456"));
			
		
		Optional<Ride> result = paymentServ.updateRidePaymentAmount("123");
		
		assertTrue(result.isPresent());
		assertTrue(result.get().isPaid());
		
		
		verify(rideRepo).save(unpaidRide);
		
		verify(messagingTemplate).convertAndSend(
			    eq("/topic/driver/driver456"),
			    argThat((ArgumentMatcher<Map<String, Object>>) notification -> {
			        Map<String, Object> map = (Map<String, Object>) notification;
			        return "Ride Payment Received".equals(map.get("title")) &&
			               "A passenger has paid for the ride.".equals(map.get("message")) &&
			               "123".equals(map.get("rideId"));
			    })
			);
		
	}	
	
	
	@Test
	void refundRide_shouldRefundRide() throws StripeException {
		
	    String rideId = "123";
		Ride ride = new Ride();
		ride.setPaid(true);
		ride.setRefunded(false);
		ride.setPaymentRequestRideId("pi_123");	
		
		
		when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
		when(rideRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
		 
		Refund mockRefund = mock(Refund.class);
		when(mockRefund.getId()).thenReturn("refund_abc");
		
		try(MockedStatic<Refund> refundStatic = mockStatic(Refund.class)) {
			refundStatic.when(() -> Refund.create(any(RefundCreateParams.class))).thenReturn(mockRefund);
			
			
			Optional<Ride> result = paymentServ.refundRide("123");
			
			
			assertTrue(result.isPresent());
			assertFalse(result.get().isPaid());
			assertTrue(result.get().isRefunded());
			
		}
	}
	
	
	 
 
	
	
	
	
	
	
	
}
