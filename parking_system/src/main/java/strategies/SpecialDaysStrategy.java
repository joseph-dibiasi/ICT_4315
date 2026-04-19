package strategies;

import java.time.Instant;
import java.time.ZoneId;

import models.Car;
import models.Money;
import models.ParkingLot;

public class SpecialDaysStrategy implements ParkingStrategy {

    private Double specialDiscount = 0.2;  // TODO: Make these values configurable upon creation of Strategy

    /* adjustCharge is a parking strategy method that is guaranteed by any class that implements the ParkingStrategy interface. 
     * This lets us create individual functionality for each strategy but utilize them all in the same way to calculate the parking charges.
     * This version is special days (or special 'day' at the moment). If the day of the month matches the special day, it applies a discount to the current charge; otherwise, 
     * the normal rate is returned. 
     */
    @Override
    public Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Instant time = entryTime != null ? entryTime : exitTime;
        if (time != null && isSpecialDay(time)) {
            return new Money(currentCharge.getDollars() * (1.0 - specialDiscount));
        }
        return currentCharge;
    }

    private boolean isSpecialDay(Instant time) {
        int day = time.atZone(ZoneId.systemDefault()).getDayOfMonth();
        return day == 25;
    }

    public Double getSpecialDiscount() {
        return specialDiscount;
    }

    public void setSpecialDiscount(Double specialDiscount) {
        this.specialDiscount = specialDiscount;
    }
}