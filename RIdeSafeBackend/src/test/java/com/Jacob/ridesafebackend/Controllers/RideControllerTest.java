package com.Jacob.ridesafebackend.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.Jacob.ridesafebackend.controllers.RideController;
import com.Jacob.ridesafebackend.models.Ride;
import com.Jacob.ridesafebackend.service.RideService;


public class RideControllerTest {

	@Test
	public void testGetRideById_found() {
	    // Arrange
	    String rideId = "123";
	    Ride fakeRide = new Ride();
	    fakeRide.setId(rideId);

	    RideService rideService = mock(RideService.class);
	    when(rideService.getRideById(rideId)).thenReturn(Optional.of(fakeRide));

	   RideController controller = new RideController(rideService,null,null,null);
	   
	   ResponseEntity<Ride>  response = controller.getRideById(rideId);
	   
	   assertEquals(HttpStatus.OK, response.getStatusCode());
	   assertEquals(rideId,response.getBody().getId());

	}
	
	@Test
	public void testSaveRide() {
		
		Ride fakeRide = new Ride();
		fakeRide.setId("123");
		
		RideService rideService = mock(RideService.class);
		
		
		when(rideService.saveRide(fakeRide)).thenReturn(fakeRide);
		
		RideController controller = new RideController(rideService,null,null,null);
		
		ResponseEntity<Map<String, Object>> response = controller.saveRide(fakeRide);
		
			Map<String,Object> body = response.getBody();
			assertEquals("Ride scheduled successfully", body.get("message"));
			assertEquals(fakeRide.getId(),body.get(fakeRide));
		   assertEquals(2,body.get("passengerAmount"));
	}
	
}
