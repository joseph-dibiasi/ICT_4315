package decorators;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import factories.DecoratorFactoryConfig;
import models.Car;
import models.Money;
import models.ParkingLot;

/*
 * Parking discounts or surcharges based on specific days of the month can be configured using this decorator. 
 * Provides a concrete implementation of the calculate method implemented by the ParkingChargeDecorator class.
 * This allows us to apply modifiers to the base parking charge calculations.
 */
public class SpecialDaysDecorator extends ParkingChargeDecorator {

    private Double rateModifier;
    private List<Integer> specialDays;

    public SpecialDaysDecorator(ParkingChargeCalculator wrappedCalculator, DecoratorFactoryConfig config) {
        super(wrappedCalculator);
        this.rateModifier = config.getRateModifier();
        this.specialDays = config.getSpecialDays();
    }

    @Override
    public Money calculate(ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Money amount = wrappedCalculator.calculate(parkingLot, car, entryTime, exitTime);
        Instant time = entryTime != null ? entryTime : exitTime;
        if (time != null && specialDays.contains(time.atZone(ZoneId.systemDefault()).getDayOfMonth())) {
            return new Money(amount.getDollars() * rateModifier);
        }
        return amount;
    }

    public Double getSpecialDiscount() {
        return rateModifier;
    }

    public void setSpecialDiscount(Double specialDiscount) {
        this.rateModifier = specialDiscount;
    }
}
