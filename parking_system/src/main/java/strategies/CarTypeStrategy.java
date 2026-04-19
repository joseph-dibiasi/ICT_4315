package strategies;

import java.time.Instant;

import enums.CarType;
import models.Car;
import models.Money;
import models.ParkingLot;

public class CarTypeStrategy implements ParkingStrategy {

    private Double rateModifier = 0.8; // default 20% discount for compact

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