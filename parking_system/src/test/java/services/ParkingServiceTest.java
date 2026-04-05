package services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        String[] params = new String[] {"John Doe", "123 Main St", "555-1234"};

        String result = parkingService.performCommand("registerCustomer", params);

        assertEquals("Customer registered successfully", result);
        verify(mockParkingOffice, times(1)).register(any(models.Customer.class));
    }

    @Test
    void testPerformCommandRegisterCarSuccess() {
        String[] params = new String[] {UUID.randomUUID().toString(), "ABC-123", CarType.SUV.name()};

        String result = parkingService.performCommand("registerCar", params);

        assertEquals("Car registered successfully", result);
        verify(mockParkingOffice, times(1)).register(any(models.Car.class));
    }

    @Test
    void testPerformCommandUnknownCommand() {
        String[] params = new String[] {};

        String result = parkingService.performCommand("unknownCommand", params);

        assertEquals("Unknown command: unknownCommand", result);
    }

    @Test
    void testPerformCommandInvalidParameters() {
        String[] params = new String[] {};
        // Missing required parameters

        String result = parkingService.performCommand("registerCustomer", params);

        assertTrue(result.startsWith("Invalid parameters:"));
        assertTrue(result.contains("Missing required parameter"));
    }
}
