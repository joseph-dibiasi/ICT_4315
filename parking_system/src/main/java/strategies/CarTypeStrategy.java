package strategies;

import java.time.Instant;
import java.util.List;

import enums.CarType;
import factories.StrategyFactoryConfig;
import models.Car;
import models.Money;
import models.ParkingLot;

public class CarTypeStrategy implements ParkingStrategy {

    private Double rateModifier;
    private List<CarType> applicableCarTypes;

    public CarTypeStrategy(StrategyFactoryConfig config) {
		this.rateModifier = config.getRateModifier();
		this.applicableCarTypes = config.getCarTypes();
	}

	/* adjustCharge is a parking strategy method that is guaranteed by any class that implements the ParkingStrategy interface. 
     * This lets us create individual functionality for each strategy but utilize them all in the same way to calculate the parking charges.
     * This version is focused around car type. If the car is of type COMPACT, it applies a rate modifier to the current charge; otherwise, 
     * it returns the current charge unchanged.
     */
    @Override
    public Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {

        if (applicableCarTypes.contains(car.getType())) {
        // rateModifier is a multiplier (e.g. 1.2 = 20% surcharge, 0.8 = 20% discount)
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