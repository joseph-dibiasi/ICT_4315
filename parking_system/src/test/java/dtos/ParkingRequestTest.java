package dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import dtos.ParkingRequest;

class ParkingRequestTest {

    @Test
    void testDefaultConstructorAndSetters() {
        ParkingRequest request = new ParkingRequest();
        request.setCommandName("CUSTOMER");
        request.setProperties(null);

        assertEquals("CUSTOMER", request.getCommandName());
        assertNotNull(request.getProperties());
        assertTrue(request.getProperties().isEmpty());
    }

    @Test
    void testConstructorAndSetterKeepProvidedProperties() {
        Properties initial = new Properties();
        initial.setProperty("firstname", "Rob");
        ParkingRequest request = new ParkingRequest("CUSTOMER", initial);

        Properties updated = new Properties();
        updated.setProperty("license", "ROB4CO");
        request.setProperties(updated);

        assertEquals("Rob", initial.getProperty("firstname"));
        assertEquals("ROB4CO", request.getProperties().getProperty("license"));
    }

    @Test
    void testConstructorReplacesNullProperties() {
        ParkingRequest request = new ParkingRequest("CUSTOMER", null);

        assertEquals("CUSTOMER", request.getCommandName());
        assertNotNull(request.getProperties());
        assertTrue(request.getProperties().isEmpty());
    }

    @Test
    void testToStringIncludesCommandAndProperties() {
        Properties properties = new Properties();
        properties.setProperty("firstname", "Rob");
        ParkingRequest request = new ParkingRequest("CUSTOMER", properties);

        String text = request.toString();

        assertTrue(text.contains("CUSTOMER"));
        assertTrue(text.contains("firstname"));
        assertTrue(text.contains("Rob"));
    }

    @Test
    void testJsonRoundTrip() {
        Properties properties = new Properties();
        properties.setProperty("firstname", "Rob");
        properties.setProperty("customer", "CUST2");
        ParkingRequest request = new ParkingRequest("CAR", properties);

        ParkingRequest rebuilt = ParkingRequest.fromJson(request.toJson());

        assertEquals("CAR", rebuilt.getCommandName());
        assertEquals("Rob", rebuilt.getProperties().getProperty("firstname"));
        assertEquals("CUST2", rebuilt.getProperties().getProperty("customer"));
    }

    @Test
    void testFromJsonWithoutPropertiesUsesEmptyProperties() {
        ParkingRequest rebuilt = ParkingRequest.fromJson("{\"commandName\":\"CUSTOMER\"}");

        assertEquals("CUSTOMER", rebuilt.getCommandName());
        assertNotNull(rebuilt.getProperties());
        assertTrue(rebuilt.getProperties().isEmpty());
    }

    @Test
    void testFromJsonHandlesNullPropertyValue() {
        ParkingRequest rebuilt = ParkingRequest.fromJson(
                "{\"commandName\":\"CUSTOMER\",\"properties\":{\"firstname\":null}}");

        assertEquals("", rebuilt.getProperties().getProperty("firstname"));
    }

    @Test
    void testFromJsonWithNonObjectPropertiesUsesEmptyProperties() {
        ParkingRequest rebuilt = ParkingRequest.fromJson(
                "{\"commandName\":\"CUSTOMER\",\"properties\":\"ignored\"}");

        assertEquals("CUSTOMER", rebuilt.getCommandName());
        assertTrue(rebuilt.getProperties().isEmpty());
    }
}
