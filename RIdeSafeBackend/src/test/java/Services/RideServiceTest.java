package Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.repositorys.RideRepository;
import com.Jacob.ridesafebackend.service.RideService;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {

	
	@Mock
	private RideRepository rideRepo;
	
	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private RideService rideServ;
	
	@Test
	void getDriverIdByRideId_ShouldReturnMapOfDriver() {
		
	     // Arrange
        String rideId = "ride123";
        String expectedDriverId = "driver789";
        Ride ride = new Ride();
        ride.setDriverId(expectedDriverId);
        when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
        
        Optional<String> result = rideServ.getDriverIdByRideId(rideId);
        
        assertTrue(result.isPresent());
        assertEquals(expectedDriverId,result.get());
	}
	
	
	
	@Test
	void getDriverIdByRideId_shouldReturnEmpty_whenRideDoesNotExist() {
		String rideId = "nothing";
		
		when(rideRepo.findById(rideId)).thenReturn(Optional.empty());
		
	
		Optional<String> result = rideServ.getDriverIdByRideId(rideId);
		
		assertFalse(result.isPresent());
	}
	
	
	@Test
	void sendPassengerRatingPrompt_shouldSendMessage_whenRideExists() {
	    // Arrange
	    String rideId = "ride123";
	    String passengerId = "pass123";

	    Ride ride = new Ride();
	    ride.setId(rideId);
	    ride.setPassengerId(passengerId);

	    when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));

	    // Act
	    rideServ.sendPassengerRatingPrompt(rideId);

	    // Assert
	    verify(messagingTemplate).convertAndSend(
	        eq("/topic/passenger/" + passengerId),
	        eq("Please rate your driver!")
	    );
	}

	
	
	
	
	
	
	
	
}
