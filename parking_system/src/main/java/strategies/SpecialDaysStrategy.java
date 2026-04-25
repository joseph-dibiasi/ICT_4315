package strategies;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import factories.StrategyFactoryConfig;
import models.Car;
import models.Money;
import models.ParkingLot;

public class SpecialDaysStrategy implements ParkingStrategy {

    private Double rateModifier;
    private List<Integer> specialDays;

    public SpecialDaysStrategy(StrategyFactoryConfig config) {
    	this.rateModifier = config.getRateModifier();
    	this.specialDays = config.getSpecialDays();
    }

	/* adjustCharge is a parking strategy method that is guaranteed by any class that implements the ParkingStrategy interface. 
     * This lets us create individual functionality for each strategy but utilize them all in the same way to calculate the parking charges.
     * This version is special days (or special 'day' at the moment). If the day of the month matches the special day, it applies a discount to the current charge; otherwise, 
     * the normal rate is returned. 
     */
    @Override
    public Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Instant time = entryTime != null ? entryTime : exitTime;
        if (time != null && isSpecialDay(time)) {
            // rateModifier is a multiplier (e.g. 1.2 = 20% surcharge, 0.8 = 20% discount)
            return new Money(currentCharge.getDollars() * rateModifier);
        }
        return currentCharge;
    }

    private boolean isSpecialDay(Instant time) {
       return this.specialDays.contains(time.atZone(ZoneId.systemDefault()).getDayOfMonth());
    }

    public Double getSpecialDiscount() {
        return rateModifier;
    }

    public void setSpecialDiscount(Double specialDiscount) {
        this.rateModifier = specialDiscount;
    }
}