package commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import enums.CarType;
import models.Car;
import services.ParkingOffice;

class RegisterCarCommandTest {

    private RegisterCarCommand command;
    private ParkingOffice mockParkingOffice;
    private java.util.Properties validParams;
    private UUID testOwnerId;

    @BeforeEach
    void setUp() {
        mockParkingOffice = org.mockito.Mockito.spy(new ParkingOffice());
        command = new RegisterCarCommand(mockParkingOffice);
        
        testOwnerId = UUID.randomUUID();
        
        validParams = new java.util.Properties();
        validParams.setProperty("ownerid", testOwnerId.toString());
        validParams.setProperty("license", "ABC-123");
        validParams.setProperty("cartype", CarType.COMPACT.name());
        org.mockito.Mockito.doReturn(new models.Car()).when(mockParkingOffice).register(org.mockito.ArgumentMatchers.any(models.Car.class));
    }

    @Test
    void testGetCommandName() {
        assertEquals("car", command.getCommandName());
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
        java.util.Properties params = new java.util.Properties();
        params.setProperty("ownerid", "");
        params.setProperty("license", "ABC-123");
        params.setProperty("cartype", CarType.COMPACT.name());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: ownerid", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingLicense() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("ownerid", testOwnerId.toString());
        params.setProperty("license", "");
        params.setProperty("cartype", CarType.COMPACT.name());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: license", exception.getMessage());
    }

    @Test
    void testCheckParametersBlankLicense() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("ownerid", testOwnerId.toString());
        params.setProperty("license", "");
        params.setProperty("cartype", CarType.COMPACT.name());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: license", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingCarType() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("ownerid", testOwnerId.toString());
        params.setProperty("license", "ABC-123");
        params.setProperty("cartype", "");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: cartype", exception.getMessage());
    }

    @Test
    void testCheckParametersNullParams() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(null));
        assertEquals("Missing required parameters", exception.getMessage());
    }

    @Test
    void testCheckParametersInsufficientParams() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("ownerid", testOwnerId.toString());
        params.setProperty("license", "ABC-123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: cartype", exception.getMessage());
    }

    @Test
    void testExecuteInvalidOwnerIdFormat() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("ownerid", "not-a-uuid");
        params.setProperty("license", "ABC-123");
        params.setProperty("cartype", CarType.SUV.name());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.execute(params));
        assertEquals("Invalid ownerid format", exception.getMessage());
    }

    @Test
    void testExecuteInvalidCarType() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("ownerid", testOwnerId.toString());
        params.setProperty("license", "ABC-123");
        params.setProperty("cartype", "invalidType");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.execute(params));
        assertEquals("Invalid Car Type. Valid types are SUV or COMPACT.", exception.getMessage());
    }

    @Test
    void testExecuteValidParameters() {
        String result = command.execute(validParams);
        
        assertTrue(result.startsWith("Car registered successfully"));
        
        // Verify that parkingOffice.register was called with a car having the correct data
        verify(mockParkingOffice, times(1)).register(any(Car.class));
    }
}
