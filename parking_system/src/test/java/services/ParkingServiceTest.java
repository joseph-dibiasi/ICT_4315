package services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commands.Command;
import commands.RegisterCarCommand;
import commands.RegisterCustomerCommand;
import models.Address;
import models.CarType;
import models.ParkingOffice;

class ParkingServiceTest {

    private ParkingService parkingService;
    private ParkingOffice mockParkingOffice;
    private RegisterCustomerCommand registerCustomerCommand;
    private RegisterCarCommand registerCarCommand;

    @BeforeEach
    void setUp() {
        mockParkingOffice = mock(ParkingOffice.class);
        parkingService = new ParkingService(mockParkingOffice);
        
        registerCustomerCommand = new RegisterCustomerCommand(mockParkingOffice);
        registerCarCommand = new RegisterCarCommand(mockParkingOffice);
        
        parkingService.register(registerCustomerCommand);
        parkingService.register(registerCarCommand);
    }

    @Test
    void testPerformCommandRegisterCustomerSuccess() {
        Properties params = new Properties();
        params.setProperty("name", "John Doe");
        Address address = new Address();
        address.setStreetAddress1("123 Main St");
        params.put("address", address);
        params.setProperty("phoneNumber", "555-1234");
        
        String result = parkingService.performCommand("registerCustomer", params);
        
        assertEquals("Customer registered successfully", result);
        verify(mockParkingOffice, times(1)).register(any(models.Customer.class));
    }

    @Test
    void testPerformCommandRegisterCarSuccess() {
        Properties params = new Properties();
        params.put("ownerId", UUID.randomUUID());
        params.setProperty("license", "ABC-123");
        params.put("carType", CarType.SUV);
        
        String result = parkingService.performCommand("registerCar", params);
        
        assertEquals("Car registered successfully", result);
        verify(mockParkingOffice, times(1)).register(any(models.Car.class));
    }

    @Test
    void testPerformCommandUnknownCommand() {
        Properties params = new Properties();
        
        String result = parkingService.performCommand("unknownCommand", params);
        
        assertEquals("Unknown command: unknownCommand", result);
    }

    @Test
    void testPerformCommandInvalidParameters() {
        Properties params = new Properties();
        // Missing required parameters
        
        String result = parkingService.performCommand("registerCustomer", params);
        
        assertTrue(result.startsWith("Invalid parameters:"));
        assertTrue(result.contains("Missing required parameter"));
    }
}
