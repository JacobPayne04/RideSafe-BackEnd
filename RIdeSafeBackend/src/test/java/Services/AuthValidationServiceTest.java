package Services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;

import com.Jacob.ridesafebackend.service.AuthValidationService;
import com.Jacob.ridesafebackend.service.jwtService;


@ExtendWith(MockitoExtension.class)
public class AuthValidationServiceTest {

    @Mock
    private jwtService jwtServ;

    @InjectMocks
    private AuthValidationService authValidationService;

    @Test
    void validateRequest_validToken_shouldPass() {
        String token = "validToken";
        String userId = "user123";
        String role = "PASSENGER";

        // Mock behavior
        when(jwtServ.extractUserId(token)).thenReturn(userId);
        when(jwtServ.extractUserRole(token)).thenReturn(role);
        when(jwtServ.isTokenValid(token, userId, role)).thenReturn(true);

        // Act + Assert — no exception means success
        assertDoesNotThrow(() -> authValidationService.validateRequest(token, userId, role));
    }
	
	
	
}
