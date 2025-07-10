package Services;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.models.Passenger;
import com.Jacob.ridesafebackend.repositorys.DriverRepository;
import com.Jacob.ridesafebackend.repositorys.PassengerRepository;
import com.Jacob.ridesafebackend.service.DriverService;
import com.Jacob.ridesafebackend.service.PassengerService;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceTest {

	@InjectMocks
	private PassengerService passengerServ;

	@Mock
	private PassengerRepository passengerRepo;
	 
	 private Passenger samplePassenger;
	 
	 @BeforeEach
	 void setup() {
		 samplePassenger = new Passenger();
		 samplePassenger.setId("driver123)");
		 samplePassenger.setEmail("test@example.com");
		 samplePassenger.setPassword("plainpassword");
	 }
	
	 
	 @Test
	 void creatPassenger_shouldHashPasswordAndSave() {
		 when(passengerRepo.save(any(Passenger.class))).thenAnswer(invocation -> invocation.getArgument(0));
		 
		 Passenger result = passengerServ.createPassenger(samplePassenger);
		 
		 assertNotEquals("plainpassword", result.getPassword()); // Password should be hashed
	     assertTrue(result.getPassword().startsWith("$2a$")); // bcrypt pattern
	     verify(passengerRepo, times(1)).save(any(Passenger.class));
		 
	 }
	 
	 
	 @Test
	 void updatePassengerStatus_shouldSavePassengerStatus() {
		 
		 Passenger existingPassenger = new Passenger();
		 existingPassenger.setId("123");
		
		 
		 when(passengerRepo.findById("123")).thenReturn(Optional.of(existingPassenger));
		 
		 passengerServ.updatePasengerStatus("123",10.0,20.0);
		 
		 
		 verify(passengerRepo).save(argThat(passenger ->
	        passenger.getLocation() != null &&
	        passenger.getLocation().getX() == 10.0 &&
	        passenger.getLocation().getY() == 20.0
	    ));;
		  
	 }
	 
	 @Test
	 void authenticatePassenger_shouldReturnTrueIfPasswordMatches() {
		 String raw = "myPassword";
		 String hashed =  BCrypt.hashpw(raw, BCrypt.gensalt());
		 
		 boolean result = passengerServ.authenticatePassenger(raw, hashed);
		 
		 assertTrue(result);
		 
		 
	 }
	 
	 
	 
	
}
