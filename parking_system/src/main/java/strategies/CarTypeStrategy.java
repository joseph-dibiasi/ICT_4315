package strategies;

import java.time.Instant;

import enums.CarType;
import models.Car;
import models.Money;
import models.ParkingLot;

public class CarTypeStrategy implements ParkingStrategy {

    private Double rateModifier = 0.8; // TODO: Make these values configurable upon creation of Strategy

    /* adjustCharge is a parking strategy method that is guaranteed by any class that implements the ParkingStrategy interface. 
     * This lets us create individual functionality for each strategy but utilize them all in the same way to calculate the parking charges.
     * This version is focused around car type. If the car is of type COMPACT, it applies a rate modifier to the current charge; otherwise, 
     * it returns the current charge unchanged.
     */
    @Override
    public Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        if (car.getType() == CarType.COMPACT) {
            return new Money(currentCharge.getDollars() * rateModifier);
        } else {
            return currentCharge;
        }
    }

    public Double getRateModifier() {
        return rateModifier;
    }

    public void setRateModifier(Double rateModifier) {
        this.rateModifier = rateModifier;
    }
}