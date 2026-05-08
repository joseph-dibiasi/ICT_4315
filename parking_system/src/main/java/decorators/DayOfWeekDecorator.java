package decorators;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import factories.DecoratorFactoryConfig;
import models.Car;
import models.Money;
import models.ParkingLot;

/*
 * Parking discounts or surcharges based on days of the week can be configured using this decorator. 
 * Provides a concrete implementation of the calculate method implemented by the ParkingChargeDecorator class.
 * This allows us to apply modifiers to the base parking charge calculations.
 */
public class DayOfWeekDecorator extends ParkingChargeDecorator {

    private Double rateModifier;
    private List<DayOfWeek> applicableDays;

    public DayOfWeekDecorator(ParkingChargeCalculator wrappedCalculator, DecoratorFactoryConfig config) {
        super(wrappedCalculator);
        this.rateModifier = config.getRateModifier();
        this.applicableDays = config.getDaysOfWeek();
    }

    @Override
    public Money calculate(ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Money amount = wrappedCalculator.calculate(parkingLot, car, entryTime, exitTime);
        Instant time = entryTime != null ? entryTime : exitTime;
        if (time != null) {
            DayOfWeek day = time.atZone(ZoneId.systemDefault()).getDayOfWeek();
            if (applicableDays.contains(day)) {
                return new Money(amount.getDollars() * rateModifier);
            }
        }
        return amount;
    }

    public Double getWeekendModifier() {
        return rateModifier;
    }

    public void setWeekendModifier(Double rateModifier) {
        this.rateModifier = rateModifier;
    }
}
