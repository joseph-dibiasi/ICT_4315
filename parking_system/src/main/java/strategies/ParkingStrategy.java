package strategies;

import java.time.Instant;

import models.Car;
import models.Money;
import models.ParkingLot;

/* 
 * Base interface class that defines the adjustCharge method that all parking strategies must implement. 
 * This allows us to create multiple different strategies for calculating parking charges,
 */
public interface ParkingStrategy {
    Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime);
}