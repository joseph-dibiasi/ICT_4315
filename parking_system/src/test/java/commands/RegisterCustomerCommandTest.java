package commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import models.Address;
import models.Address.AddressBuilder;
import models.Customer;
import models.ParkingOffice;

class RegisterCustomerCommandTest {

    private RegisterCustomerCommand command;
    private ParkingOffice mockParkingOffice;
    private String[] validParams;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        mockParkingOffice = mock(ParkingOffice.class);
        command = new RegisterCustomerCommand(mockParkingOffice);
        AddressBuilder addressBuilder = new AddressBuilder();
        testAddress = addressBuilder.streetAddress1("123 Main St").city("Test City").state("TS").zipCode("12345").build();
        
        validParams = new String[] {"John Doe", "123 Main St", "555-1234"};
    }

    @Test
    void testGetCommandName() {
        assertEquals("registerCustomer", command.getCommandName());
    }

    @Test
    void testGetDisplayName() {
        assertEquals("Register Customer", command.getDisplayName());
    }

    @Test
    void testCheckParametersValid() {
        assertDoesNotThrow(() -> command.checkParameters(validParams));
    }

    @Test
    void testCheckParametersMissingName() {
        String[] params = new String[] {"", "123 Main St", "555-1234"};

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: name", exception.getMessage());
    }

    @Test
    void testCheckParametersBlankName() {
        String[] params = new String[] {"", "123 Main St", "555-1234"};

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: name", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingAddress() {
        String[] params = new String[] {"John Doe", "", "555-1234"};
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: address", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingPhone() {
        String[] params = new String[] {"John Doe", "123 Main St", ""};

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: phoneNumber", exception.getMessage());
    }

    @Test
    void testCheckParametersBlankPhone() {
        String[] params = new String[] {"John Doe", "123 Main St", ""};
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: phoneNumber", exception.getMessage());
    }

    @Test
    void testExecuteValidParameters() {
        String result = command.execute(validParams);
        
        assertEquals("Customer registered successfully", result);
        
        // Verify that parkingOffice.register was called with a customer having the correct data
        verify(mockParkingOffice, times(1)).register(any(Customer.class));
    }
}
