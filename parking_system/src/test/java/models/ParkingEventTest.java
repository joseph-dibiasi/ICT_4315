package models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class ParkingEventTest {

    @Test
    public void testConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.of(2020, 1, 1, 12, 0);
        ParkingLot lot = new ParkingLot();
        Car car = new Car();

        ParkingEvent event = new ParkingEvent(ParkingEvent.EventType.ENTRY, now, lot, car);

        assertEquals(ParkingEvent.EventType.ENTRY, event.getType());
        assertEquals(now, event.getDateTime());
        assertEquals(car, event.getCar());
        assertEquals(lot, event.getLot());
    }

    @Test
    public void testToInstantConversion() {
        LocalDateTime now = LocalDateTime.of(2020, 6, 15, 8, 30);
        ParkingLot lot = new ParkingLot();
        Car car = new Car();

        ParkingEvent event = new ParkingEvent(ParkingEvent.EventType.EXIT, now, lot, car);

        Instant expected = now.atZone(ZoneId.systemDefault()).toInstant();
        assertEquals(expected, event.toInstant());
    }

    @Test
    public void testToInstantHandlesNullDate() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();

        ParkingEvent event = new ParkingEvent(ParkingEvent.EventType.EXIT, null, lot, car);
        assertNull(event.getDateTime());
        assertNull(event.toInstant());
    }
}
