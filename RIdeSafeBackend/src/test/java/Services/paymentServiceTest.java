package Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.DriverRepository;
import com.Jacob.ridesafebackend.repositorys.RideRepository;
import com.Jacob.ridesafebackend.service.DriverService;
import com.Jacob.ridesafebackend.service.PaymentService;
import com.Jacob.ridesafebackend.service.RideService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

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
	
	
	
	
	
}
