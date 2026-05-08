package decorators;

import java.time.Instant;

import models.Car;
import models.Money;
import models.ParkingLot;

/* 
 * Base interface class that defines the calculate method that all parking charges must implement. 
 * This allows us to create complex parking lot rate strategies based off small decorators.
 */
public interface ParkingChargeCalculator {
    Money calculate(ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime);
}
