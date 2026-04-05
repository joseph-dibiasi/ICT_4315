package commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Car;
import models.CarType;
import models.ParkingOffice;

class RegisterCarCommandTest {

    private RegisterCarCommand command;
    private ParkingOffice mockParkingOffice;
    private Properties validParams;
    private UUID testOwnerId;

    @BeforeEach
    void setUp() {
        mockParkingOffice = mock(ParkingOffice.class);
        command = new RegisterCarCommand(mockParkingOffice);
        
        testOwnerId = UUID.randomUUID();
        
        validParams = new Properties();
        validParams.put("ownerId", testOwnerId);
        validParams.setProperty("license", "ABC-123");
        validParams.put("carType", CarType.COMPACT);
    }

    @Test
    void testGetCommandName() {
        assertEquals("registerCar", command.getCommandName());
    }

    @Test
    void testGetDisplayName() {
        assertEquals("Register Car", command.getDisplayName());
    }

    @Test
    void testCheckParametersValid() {
        assertDoesNotThrow(() -> command.checkParameters(validParams));
    }

    @Test
    void testCheckParametersMissingOwnerId() {
        Properties params = new Properties();
        params.setProperty("license", "ABC-123");
        params.put("carType", CarType.COMPACT);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: ownerId", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingLicense() {
        Properties params = new Properties();
        params.put("ownerId", testOwnerId);
        params.put("carType", CarType.COMPACT);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: license", exception.getMessage());
    }

    @Test
    void testCheckParametersBlankLicense() {
        Properties params = new Properties();
        params.put("ownerId", testOwnerId);
        params.setProperty("license", "");
        params.put("carType", CarType.COMPACT);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: license", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingCarType() {
        Properties params = new Properties();
        params.put("ownerId", testOwnerId);
        params.setProperty("license", "ABC-123");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: carType", exception.getMessage());
    }

    @Test
    void testExecuteValidParameters() {
        String result = command.execute(validParams);
        
        assertEquals("Car registered successfully", result);
        
        // Verify that parkingOffice.register was called with a car having the correct data
        verify(mockParkingOffice, times(1)).register(any(Car.class));
    }
}
