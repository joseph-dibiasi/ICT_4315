package dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dtos.ParkingResponse;

class ParkingResponseTest {

    @Test
    void testDefaultConstructorAndSetters() {
        ParkingResponse response = new ParkingResponse();
        response.setStatusCode(201);
        response.setMessage("created");

        assertEquals(201, response.getStatusCode());
        assertEquals("created", response.getMessage());
        assertNotNull(response.toJson());
    }

    @Test
    void testToStringIncludesStatusAndMessage() {
        ParkingResponse response = new ParkingResponse(200, "Customer registered successfully");

        String text = response.toString();

        assertTrue(text.contains("200"));
        assertTrue(text.contains("Customer registered successfully"));
    }

    @Test
    void testJsonRoundTrip() {
        ParkingResponse response = new ParkingResponse(404, "Unknown customer");

        ParkingResponse rebuilt = ParkingResponse.fromJson(response.toJson());

        assertEquals(404, rebuilt.getStatusCode());
        assertEquals("Unknown customer", rebuilt.getMessage());
    }

    @Test
    void testJsonRoundTripWithNullMessage() {
        ParkingResponse rebuilt = ParkingResponse.fromJson("{\"statusCode\":202,\"message\":null}");

        assertEquals(202, rebuilt.getStatusCode());
        assertEquals(null, rebuilt.getMessage());
    }
}
