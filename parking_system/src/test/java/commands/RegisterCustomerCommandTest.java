package commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import models.Address;
import models.Address.AddressBuilder;
import services.ParkingOffice;
import models.Customer;

class RegisterCustomerCommandTest {

    private RegisterCustomerCommand command;
    private ParkingOffice mockParkingOffice;
    private java.util.Properties validParams;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        mockParkingOffice = org.mockito.Mockito.spy(new ParkingOffice());
        command = new RegisterCustomerCommand(mockParkingOffice);
        AddressBuilder addressBuilder = new AddressBuilder();
        testAddress = addressBuilder.streetAddress1("123 Main St").city("Test City").state("TS").zipCode("12345").build();
        
        validParams = new java.util.Properties();
        validParams.setProperty("name", "John Doe");
        validParams.setProperty("address", "123 Main St");
        validParams.setProperty("phonenumber", "555-1234");
    }

    @Test
    void testGetCommandName() {
        assertEquals("customer", command.getCommandName());
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
    void testCheckParametersNullArray() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(null));
        assertEquals("Missing required parameters", exception.getMessage());
    }

    @Test
    void testCheckParametersTooShort() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("name", "John Doe");
        params.setProperty("address", "123 Main St");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameters", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingName() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("name", "");
        params.setProperty("address", "123 Main St");
        params.setProperty("phonenumber", "555-1234");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: name", exception.getMessage());
    }

    @Test
    void testCheckParametersBlankName() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("name", "");
        params.setProperty("address", "123 Main St");
        params.setProperty("phonenumber", "555-1234");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: name", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingAddress() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("name", "John Doe");
        params.setProperty("address", "");
        params.setProperty("phonenumber", "555-1234");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: address", exception.getMessage());
    }

    @Test
    void testCheckParametersBlankAddress() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("name", "John Doe");
        params.setProperty("address", "");
        params.setProperty("phonenumber", "555-1234");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: address", exception.getMessage());
    }

    @Test
    void testCheckParametersMissingPhone() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("name", "John Doe");
        params.setProperty("address", "123 Main St");
        params.setProperty("phonenumber", "");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: phoneNumber", exception.getMessage());
    }

    @Test
    void testCheckParametersBlankPhone() {
        java.util.Properties params = new java.util.Properties();
        params.setProperty("name", "John Doe");
        params.setProperty("address", "123 Main St");
        params.setProperty("phonenumber", "");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> command.checkParameters(params));
        assertEquals("Missing required parameter: phoneNumber", exception.getMessage());
    }

    @Test
    void testExecuteValidParameters() {
        String result = command.execute(validParams);
        
        assertTrue(result.startsWith("Customer registered successfully"));
        
        // Verify that parkingOffice.register was called with a customer having the correct data
        verify(mockParkingOffice, times(1)).register(any(Customer.class));
    }
}
