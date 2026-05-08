package decorators;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import factories.DecoratorFactoryConfig;
import models.Car;
import models.Money;
import models.ParkingLot;

/*
 * Parking discounts or surcharges based on the time of day can be configured using this decorator. 
 * Provides a concrete implementation of the calculate method implemented by the ParkingChargeDecorator class.
 * This allows us to apply modifiers to the base parking charge calculations.
 */
public class TimeOfDayDecorator extends ParkingChargeDecorator {

    private Double rateModifier;
    private Instant[] timeOfDayRange;

    public TimeOfDayDecorator(ParkingChargeCalculator wrappedCalculator, DecoratorFactoryConfig config) {
        super(wrappedCalculator);
        this.rateModifier = config.getRateModifier();
        this.timeOfDayRange = config.getTimeOfDayRange();
    }

    @Override
    public Money calculate(ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Money amount = wrappedCalculator.calculate(parkingLot, car, entryTime, exitTime);
        Instant time = entryTime != null ? entryTime : exitTime;
        if (time != null && isPeakTime(time)) {
            return new Money(amount.getDollars() * rateModifier);
        }
        return amount;
    }

    private boolean isPeakTime(Instant time) {
        Instant startTime = timeOfDayRange[0];
        Instant endTime = timeOfDayRange[1];
        LocalTime localTime = time.atZone(ZoneId.systemDefault()).toLocalTime();
        return localTime.isAfter(startTime.atZone(ZoneId.systemDefault()).toLocalTime())
                && localTime.isBefore(endTime.atZone(ZoneId.systemDefault()).toLocalTime());
    }

    public Double getPeakSurcharge() {
        return rateModifier;
    }

    public void setPeakSurcharge(Double peakSurcharge) {
        this.rateModifier = peakSurcharge;
    }
}
