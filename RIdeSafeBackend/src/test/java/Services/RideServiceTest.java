package Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.models.Ride.RideStatus;
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
		assertEquals(expectedDriverId, result.get());
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
		verify(messagingTemplate).convertAndSend(eq("/topic/passenger/" + passengerId), eq("Please rate your driver!"));
	}

	@Test
	void updateRideStatus_validRide_setsCorrectStatus() {
		// Arrange
		String rideId = "123";
		Ride ride = new Ride();
		ride.setId(rideId);

		Ride.RideStatus newStatus = Ride.RideStatus.COMPLETED;

		when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));

		// Act
		rideServ.updateRideStatus(rideId, newStatus);

		// Assert — verify save was called with correct status
		verify(rideRepo).save(argThat(savedRide -> savedRide.getStatus() == Ride.RideStatus.COMPLETED));
	}

	@Test
	void acceptRide_AcceptsRideAndTurnsONGOING() {
		String rideId = "123";
		String driverId = "driverABC";

		Ride ride = new Ride();
		ride.setId(rideId);
		when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
		when(rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.ONGOING)).thenReturn(List.of()); // no ongoing
																											// rides
		when(rideRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

		Ride result = rideServ.acceptRide(rideId, driverId);

		assertEquals(driverId, result.getDriverId());
		assertEquals(Ride.RideStatus.ONGOING, result.getStatus());
		assertEquals(0, result.getQueuePosition());
	}

	@Test
	void acceptRide_AcceptsRideAndTurnsINQUEUE() {
		String rideId = "123";
		String driverId = "driverABC";

		Ride ride = new Ride();
		ride.setId(rideId);

		Ride ongoingRide = new Ride();
		ride.setId(rideId);
		when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
		when(rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.ONGOING)).thenReturn(List.of(ongoingRide));
		when(rideRepo.countByDriverIdAndStatus(driverId, Ride.RideStatus.INQUEUE)).thenReturn(2L);
		when(rideRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

		Ride result = rideServ.acceptRide(rideId, driverId);

		assertEquals(driverId, result.getDriverId());
		assertEquals(Ride.RideStatus.INQUEUE, result.getStatus());
		assertEquals(3, result.getQueuePosition());
	}

	@Test
	void completeRide_ShouldMarkRideAsComplete() {

		String rideId = "123";
		String driverId = "abc_123";
		Ride ride = new Ride();
		ride.setId(rideId);
		
		Ride listRides = new Ride();

		when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
		when(rideRepo.findByDriverIdAndStatus(driverId, Ride.RideStatus.INQUEUE)).thenReturn(List.of(ride));

		Ride result = rideServ.completeRide(rideId, driverId);

		assertEquals(driverId, result.getDriverId());
		assertEquals(Ride.RideStatus.COMPLETED, result.getStatus());

	}

}
