package decorators;

import java.time.Instant;
import java.util.List;

import enums.CarType;
import factories.DecoratorFactoryConfig;
import models.Car;
import models.Money;
import models.ParkingLot;

/*
 * Parking discounts or surcharges based on specific types of cars can be configured using this decorator. 
 * Provides a concrete implementation of the calculate method implemented by the ParkingChargeDecorator class.
 * This allows us to apply modifiers to the base parking charge calculations.
 */
public class CarTypeDecorator extends ParkingChargeDecorator {

    private Double rateModifier;
    private List<CarType> applicableCarTypes;

    public CarTypeDecorator(ParkingChargeCalculator wrappedCalculator, DecoratorFactoryConfig config) {
        super(wrappedCalculator);
        this.rateModifier = config.getRateModifier();
        this.applicableCarTypes = config.getCarTypes();
    }

    @Override
    public Money calculate(ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Money amount = wrappedCalculator.calculate(parkingLot, car, entryTime, exitTime);
        if (applicableCarTypes.contains(car.getType())) {
            return new Money(amount.getDollars() * rateModifier);
        }
        return amount;
    }

    public Double getRateModifier() {
        return rateModifier;
    }

    public void setRateModifier(Double rateModifier) {
        this.rateModifier = rateModifier;
    }
}
