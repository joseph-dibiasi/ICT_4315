package services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commands.Command;
import commands.RegisterCarCommand;
import commands.RegisterCustomerCommand;
import enums.CarType;
import models.Address;
import services.ParkingOffice;

class ParkingServiceTest {

    private ParkingService parkingService;
    private ParkingOffice mockParkingOffice;
    private RegisterCustomerCommand registerCustomerCommand;
    private RegisterCarCommand registerCarCommand;

    @BeforeEach
    void setUp() {
        mockParkingOffice = spy(new ParkingOffice());
        parkingService = new ParkingService(mockParkingOffice);
        
        registerCustomerCommand = new RegisterCustomerCommand(mockParkingOffice);
        registerCarCommand = new RegisterCarCommand(mockParkingOffice);
        
        parkingService.register(registerCustomerCommand);
        parkingService.register(registerCarCommand);
    }

    @Test
    void testPerformCommandRegisterCustomerSuccess() {
        java.util.Properties props = new java.util.Properties();
        props.setProperty("name", "John Doe");
        props.setProperty("address", "123 Main St");
        props.setProperty("phonenumber", "555-1234");

        dtos.ParkingResponse response = parkingService.performCommand("customer", props);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getMessage().startsWith("Customer registered successfully"));
        verify(mockParkingOffice, times(1)).register(any(models.Customer.class));
    }

    @Test
    void testPerformCommandRegisterCarSuccess() {
        // First register a customer so the owner exists
        java.util.Properties custProps = new java.util.Properties();
        custProps.setProperty("name", "Jane Doe");
        custProps.setProperty("address", "456 Elm St");
        custProps.setProperty("phonenumber", "555-5678");
        dtos.ParkingResponse custResponse = parkingService.performCommand("customer", custProps);
        assertEquals(200, custResponse.getStatusCode());

        // Retrieve the registered customer's UUID from the parking office
        UUID ownerId = mockParkingOffice.getCustomerIds().get(0);

        java.util.Properties props = new java.util.Properties();
        props.setProperty("ownerid", ownerId.toString());
        props.setProperty("license", "ABC-123");
        props.setProperty("cartype", CarType.SUV.name());

        dtos.ParkingResponse response = parkingService.performCommand("car", props);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getMessage().startsWith("Car registered successfully"));
        verify(mockParkingOffice, times(1)).register(any(models.Car.class));
    }

    @Test
    void testPerformCommandUnknownCommand() {
        java.util.Properties props = new java.util.Properties();

        dtos.ParkingResponse response = parkingService.performCommand("unknownCommand", props);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getMessage().contains("Invalid Command: unknownCommand"));
    }

    @Test
    void testPerformCommandInvalidParameters() {
        java.util.Properties props = new java.util.Properties();
        // Missing required parameters

        dtos.ParkingResponse response = parkingService.performCommand("customer", props);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getMessage().contains("Missing required parameter") || response.getMessage().contains("Missing required parameters"));
    }

    @Test
    void testCommandRegistryIsInstanceScoped() {
        ParkingOffice firstOffice = new ParkingOffice();
        ParkingService firstService = new ParkingService(firstOffice);
        firstService.register(new Command() {
            @Override
            public String getCommandName() {
                return "LOCAL";
            }

            @Override
            public String getDisplayName() {
                return "Local";
            }

            @Override
            public void checkParameters(Properties params) {
            }

            @Override
            public String execute(Properties params) {
                return "ok";
            }
        });

        ParkingService secondService = new ParkingService(new ParkingOffice());
        dtos.ParkingResponse response = secondService.performCommand("LOCAL", new Properties());

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getMessage().contains("Invalid Command: LOCAL"));
    }

    @Test
    void testHandleJsonInputSuccess() {
        ParkingOffice office = new ParkingOffice();
        ParkingService service = new ParkingService(office);

        service.register(new Command() {
            @Override
            public String getCommandName() {
                return "ECHO2";
            }

            @Override
            public String getDisplayName() {
                return "Echo2";
            }

            @Override
            public void checkParameters(java.util.Properties params) throws IllegalArgumentException {
            }

            @Override
            public String execute(java.util.Properties params) {
                return "ok:" + params.getProperty("x", "");
            }
        });

        java.util.Properties props = new java.util.Properties();
        props.setProperty("x", "1");
        dtos.ParkingRequest req = new dtos.ParkingRequest("ECHO2", props);
        ByteArrayInputStream in = new ByteArrayInputStream(req.toJson().getBytes(java.nio.charset.StandardCharsets.UTF_8));

        dtos.ParkingResponse resp = service.handleJsonInput(in);
        assertEquals(200, resp.getStatusCode());
        assertEquals("ok:1", resp.getMessage());
        assertSame(office, service.getParkingOffice());
    }

    @Test
    void testReadAllReadsWholeStream() throws Exception {
        ParkingService service = new ParkingService(new ParkingOffice());
        Method readAll = ParkingService.class.getDeclaredMethod("readAll", java.io.InputStream.class);
        readAll.setAccessible(true);

        String body = (String) readAll.invoke(service,
                new java.io.InputStream() {
                    private final byte[] data = "hello world".getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    private int index;

                    @Override
                    public int read() {
                        if (index >= data.length) {
                            return -1;
                        }
                        return data[index++];
                    }
                });

        assertEquals("hello world", body);
    }
}

// Additional coverage tests moved here from ParkingServiceExtraTest
class ParkingServiceTest_Extras {

    @Test
    void testPerformCommandWithCustomCommandThrowing_additional() {
        ParkingOffice office = new ParkingOffice();
        ParkingService service = new ParkingService(office);

        service.register(new Command() {
            @Override
            public String getCommandName() {
                return "BOOM";
            }

            @Override
            public String getDisplayName() {
                return "Boom";
            }

            @Override
            public void checkParameters(java.util.Properties params) throws IllegalArgumentException {
                throw new IllegalArgumentException("bad things");
            }

            @Override
            public String execute(java.util.Properties params) {
                return "should not run";
            }
        });

        dtos.ParkingResponse response = service.performCommand("BOOM", new java.util.Properties());
        assertEquals(400, response.getStatusCode());
        assertEquals("bad things", response.getMessage());
    }

    @Test
    void testPerformCommandExecuteSuccess_and_getParkingOffice() {
        ParkingOffice office = new ParkingOffice();
        ParkingService service = new ParkingService(office);

        service.register(new Command() {
            @Override
            public String getCommandName() {
                return "ECHO";
            }

            @Override
            public String getDisplayName() {
                return "Echo";
            }

            @Override
            public void checkParameters(java.util.Properties params) throws IllegalArgumentException {
                // accept anything
            }

            @Override
            public String execute(java.util.Properties params) {
                return "echo:" + params.getProperty("msg", "");
            }
        });

        java.util.Properties props = new java.util.Properties();
        props.setProperty("msg", "hello");
        dtos.ParkingResponse resp = service.performCommand("ECHO", props);
        assertEquals(200, resp.getStatusCode());
        assertEquals("echo:hello", resp.getMessage());

        // also cover getParkingOffice()
        assertNotNull(service.getParkingOffice());
    }

    @Test
    void testPerformCommandInvalidCommand() {
        ParkingOffice office = new ParkingOffice();
        ParkingService service = new ParkingService(office);

        dtos.ParkingResponse resp = service.performCommand("NOPE", new Properties());
        assertEquals(400, resp.getStatusCode());
        assertTrue(resp.getMessage().contains("Invalid Command: NOPE"));
    }

    @Test
    void testPerformCommandCheckParametersThrows() {
        ParkingOffice office = new ParkingOffice();
        ParkingService service = new ParkingService(office);

        service.register(new Command() {
            @Override
            public String getCommandName() { return "BADD"; }
            @Override
            public String getDisplayName() { return "BadD"; }
            @Override
            public void checkParameters(Properties params) { throw new IllegalArgumentException("bad params"); }
            @Override
            public String execute(Properties params) { return ""; }
        });

        dtos.ParkingResponse resp = service.performCommand("BADD", new Properties());
        assertEquals(400, resp.getStatusCode());
        assertEquals("bad params", resp.getMessage());
    }

    @Test
    void testRegisterDefaultCommandsRegistersCommands() {
        ParkingOffice office = new ParkingOffice();
        ParkingService service = new ParkingService(office);
        service.registerDefaultCommands();

        // Calling CUSTOMER without parameters should trigger checkParameters IllegalArgumentException
        dtos.ParkingResponse resp = service.performCommand("CUSTOMER", new Properties());
        assertEquals(400, resp.getStatusCode());
        assertNotNull(resp.getMessage());
    }

    @Test
    void testPerformCommandAcceptsNullParams() {
        ParkingOffice office = new ParkingOffice();
        ParkingService service = new ParkingService(office);
        service.register(new Command() {
            @Override
            public String getCommandName() { return "NOPARAM"; }
            @Override
            public String getDisplayName() { return "NoParam"; }
            @Override
            public void checkParameters(Properties params) { /* accept null */ }
            @Override
            public String execute(Properties params) { return "ok"; }
        });

        dtos.ParkingResponse resp = service.performCommand("NOPARAM", null);
        assertEquals(200, resp.getStatusCode());
        assertEquals("ok", resp.getMessage());
    }

}
