package strategies;

import java.time.Instant;

import models.Car;
import models.Money;
import models.ParkingLot;

public interface ParkingStrategy {
    Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime);
}