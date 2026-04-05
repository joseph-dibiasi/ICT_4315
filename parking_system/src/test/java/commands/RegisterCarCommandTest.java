package commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Car;
import models.CarType;
import models.ParkingOffice;

class RegisterCarCommandTest {

    private RegisterCarCommand command;
    private ParkingOffice mockParkingOffice;
    private String[] validParams;
    private UUID testOwnerId;

    @BeforeEach
    void setUp() {
        mockParkingOffice = mock(ParkingOffice.class);
        command = new RegisterCarCommand(mockParkingOffice);
        
        testOwnerId = UUID.randomUUID();
        
        validParams = new String[] {testOwnerId.toString(), "ABC-123", CarType.COMPACT.name()};
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
        String[] params = new String[] {"", "ABC-123", CarType.COMPACT.name()};
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: ownerId", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingLicense() {
        String[] params = new String[] {testOwnerId.toString(), "", CarType.COMPACT.name()};
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: license", exception.getMessage());
    }

    @Test
    void testCheckParametersBlankLicense() {
        String[] params = new String[] {testOwnerId.toString(), "", CarType.COMPACT.name()};
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: license", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingCarType() {
        String[] params = new String[] {testOwnerId.toString(), "ABC-123", ""};
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: carType", exception.getMessage());
    }

    @Test
    void testCheckParametersNullParams() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(null));
        assertEquals("Missing required parameter: ownerId, license, or carType", exception.getMessage());
    }

    @Test
    void testCheckParametersInsufficientParams() {
        String[] params = new String[] {testOwnerId.toString(), "ABC-123"};
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: ownerId, license, or carType", exception.getMessage());
    }

    @Test
    void testExecuteInvalidOwnerIdFormat() {
        String[] params = new String[] {"not-a-uuid", "ABC-123", CarType.SUV.name()};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.execute(params));
        assertEquals("Invalid ownerId format", exception.getMessage());
    }

    @Test
    void testExecuteInvalidCarType() {
        String[] params = new String[] {testOwnerId.toString(), "ABC-123", "invalidType"};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.execute(params));
        assertEquals("Invalid carType", exception.getMessage());
    }

    @Test
    void testExecuteValidParameters() {
        String result = command.execute(validParams);
        
        assertEquals("Car registered successfully", result);
        
        // Verify that parkingOffice.register was called with a car having the correct data
        verify(mockParkingOffice, times(1)).register(any(Car.class));
    }
}
