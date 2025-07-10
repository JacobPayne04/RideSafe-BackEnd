package Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.Jacob.ridesafebackend.dto.DriverRequiredInformationDTO;
import com.Jacob.ridesafebackend.models.Driver;
import com.Jacob.ridesafebackend.repositorys.DriverRepository;
import com.Jacob.ridesafebackend.service.DriverService;

import java.util.Optional;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

	
	 @Mock
	 private DriverRepository driverRepo;
	
	 
	 @Mock
	 private DriverService driverServ;
	 
	 @InjectMocks
	 DriverService driverService;
	 
	 private Driver sampleDriver;
	 
	 @BeforeEach
	 void setup() {
		 sampleDriver = new Driver();
		 sampleDriver.setId("driver123)");
		 sampleDriver.setEmail("test@example.com");
		 sampleDriver.setPassword("plainpassword");
	 }
	 
	 @Test
	 void creatDriver_shouldHashPasswordAndSave() {
		 when(driverRepo.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));
		 
		 Driver result = driverService.creatDriver(sampleDriver);
		 
		 assertNotEquals("plainpassword", result.getPassword()); // Password should be hashed
	     assertTrue(result.getPassword().startsWith("$2a$")); // bcrypt pattern
	     verify(driverRepo, times(1)).save(any(Driver.class));
		 
	 }
	 
	 
	 @Test
	 void processDriverRequiredInformationSignup_shouldUpdateDriverAndSaveFiles() throws IOException {
	     // ----- Arrange -----
	     DriverRequiredInformationDTO info = mock(DriverRequiredInformationDTO.class);
	     MultipartFile dlFile = mock(MultipartFile.class);
	     MultipartFile studentIdFile = mock(MultipartFile.class);

	     when(info.getDriverid()).thenReturn("123");
	     when(info.getFirstName()).thenReturn("Jacob");
	     when(info.getLastName()).thenReturn("Payne");
	     when(info.getLicensePlate()).thenReturn("1JN12");
	     when(info.isAcceptedTerms()).thenReturn(true);
	     when(info.geteSign()).thenReturn("Signed");

	     Driver existingDriver = new Driver();
	     existingDriver.setId("123");

	     when(driverRepo.findById("123")).thenReturn(Optional.of(existingDriver));

	     // mock file behavior
	     when(dlFile.isEmpty()).thenReturn(false);
	     when(dlFile.getOriginalFilename()).thenReturn("dl.png");
	     when(dlFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("fakecontent".getBytes()));

	     when(studentIdFile.isEmpty()).thenReturn(false);
	     when(studentIdFile.getOriginalFilename()).thenReturn("id.png");
	     when(studentIdFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("fakecontent".getBytes()));

	     // ----- Act -----
	     driverService.processDriverRequiredInformationSignup(info, dlFile, studentIdFile);

	     // ----- Assert -----
	     verify(driverRepo).save(argThat(driver -> 
	         driver.getFirstName().equals("Jacob") &&
	         driver.getLastName().equals("Payne") &&
	         driver.getLicensePlate().equals("1JN12") &&
	         driver.getDlFileUrl().contains("dl.png") &&
	         driver.getStudentIdFileUrl().contains("id.png") &&
	         driver.isAcceptedTerms() &&
	         driver.geteSign().equals("Signed")
	     ));
	 }

	 
	 @Test
	 void updateStatus_ShouldUpdateOnlineAndLocation() {
		    // Arrange
		    Driver existingDriver = new Driver();
		    existingDriver.setId("123");
		    existingDriver.setIsOnline(false);

		    when(driverRepo.findById("123")).thenReturn(Optional.of(existingDriver));

		    // Act
		    driverService.updateStatus("123", true, 10.0, 20.0);

		    // Assert
		    verify(driverRepo).save(argThat(driver ->
		        driver.isOnline() &&
		        driver.getLocation().getX() == 10.0 &&
		        driver.getLocation().getY() == 20.0
		    ));
		}
		 
		 
		 
		 
	 
	 @Test
	 void updateDriver_ShouldUpdateFieldsCorrectly() {
	     // Arrange
	     Driver existingDriver = new Driver();
	     existingDriver.setId("123");
	     existingDriver.setFirstName("OldFirst");
	     existingDriver.setLastName("OldLast");
	     existingDriver.setEmail("old@example.com");
	     existingDriver.setLicensePlate("OLD123");
	     existingDriver.setDriverRate(1);

	     Driver updatedDriver = new Driver();
	     updatedDriver.setFirstName("Jacob");
	     updatedDriver.setLastName("Payne");
	     updatedDriver.setEmail("jacob@example.com");
	     updatedDriver.setLicensePlate("1JN12");
	     updatedDriver.setDriverRate(2);
	     updatedDriver.setPassword("newpass");

	     when(driverRepo.findById("123")).thenReturn(Optional.of(existingDriver));
	     when(driverRepo.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

	     // Act
	     Driver result = driverService.updateDriver("123", updatedDriver);

	     // Assert
	     assertEquals("Jacob", result.getFirstName());
	     assertEquals("Payne", result.getLastName());
	     assertEquals("jacob@example.com", result.getEmail());
	     assertEquals("1JN12", result.getLicensePlate());
	     assertEquals(4.5, result.getDriverRate());
	     assertNotEquals("newpass", result.getPassword()); // should be hashed
	     assertTrue(result.getPassword().startsWith("$2a$")); // bcrypt hash format

	     verify(driverRepo).save(any(Driver.class));
	 }
		 
	 
}
