package models;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Represents a parking event (entry or exit).
 */
public class ParkingEvent {

    public static enum EventType {
        ENTRY, EXIT
    }

    private final EventType type;
    private final LocalDateTime dateTime;
    private final Car car;
    private final ParkingLot lot;

    public ParkingEvent(EventType type, LocalDateTime dateTime, ParkingLot lot, Car car) {
        this.type = type;
        this.dateTime = dateTime;
        this.lot = lot;
        this.car = car;
    }

    public EventType getType() {
        return type;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Car getCar() {
        return car;
    }

    public ParkingLot getLot() {
        return lot;
    }

    public Instant toInstant() {
        return dateTime == null ? null : dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();
    }
}
