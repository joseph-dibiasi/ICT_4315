package strategies;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import models.Car;
import models.Money;
import models.ParkingLot;

public class DayOfWeekStrategy implements ParkingStrategy {

    private Double rateModifier;
    private List<DayOfWeek> applicableDays;

    public DayOfWeekStrategy(StrategyConfig config) {
		this.rateModifier = config.getRateModifier();
		this.applicableDays = config.getDaysOfWeek();
	}

	/* adjustCharge is a parking strategy method that is guaranteed by any class that implements the ParkingStrategy interface. 
     * This lets us create individual functionality for each strategy but utilize them all in the same way to calculate the parking charges.
     * This version is focused around the day of the week. If the day of the week matches the discounted day, it applies a rate modifier to the current charge; otherwise, 
     * it returns the current charge unchanged. Currently this is a little liberal with the discount in regards to entry time vs exit time. 
     * May be more strict in future implementations for users parking multiple days.
     */
    @Override
    public Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Instant time = entryTime != null ? entryTime : exitTime;
        if (time != null) {
            DayOfWeek day = time.atZone(ZoneId.systemDefault()).getDayOfWeek();
            if (applicableDays.contains(day)) {
                return new Money(currentCharge.getDollars() * rateModifier);
            }
        }
        return currentCharge;
    }

    public Double getWeekendModifier() {
        return rateModifier;
    }

    public void setWeekendModifier(Double rateModifier) {
        this.rateModifier = rateModifier;
    }
}