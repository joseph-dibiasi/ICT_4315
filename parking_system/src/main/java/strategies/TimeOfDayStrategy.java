package strategies;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import models.Car;
import models.Money;
import models.ParkingLot;

public class TimeOfDayStrategy implements ParkingStrategy {

    private Double peakSurcharge = 0.2; // TODO: Make these values configurable upon creation of Strategy

    /* adjustCharge is a parking strategy method that is guaranteed by any class that implements the ParkingStrategy interface. 
     * This lets us create individual functionality for each strategy but utilize them all in the same way to calculate the parking charges.
     * This version is focused around time of day. The idea here is to provide twin tools of either increasing or decreasing the time based on the time of day.
     * The individual strategy method was designed with combinations in mind, peak times could be discounted on special days (or increased on the popular lots).
     * While this is currently of more limited use, once the values are configurable upon creation of the strategy, 
     * it could be used in a variety of ways to create different strategies based on time of day, potentially within the same lot.
     */
    @Override
    public Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Instant time = entryTime != null ? entryTime : exitTime;
        if (time == null) return currentCharge;

        double multiplier = 1.0;
        if (isPeakTime(time)) {
            multiplier += peakSurcharge;
        }
        
        return new Money(currentCharge.getDollars() * multiplier);
    }

    private boolean isPeakTime(Instant time) {
        LocalTime localTime = time.atZone(ZoneId.systemDefault()).toLocalTime();
        return localTime.isAfter(LocalTime.of(8, 0)) && localTime.isBefore(LocalTime.of(18, 0));
    }


    public Double getPeakSurcharge() {
        return peakSurcharge;
    }

    public void setPeakSurcharge(Double peakSurcharge) {
        this.peakSurcharge = peakSurcharge;
    }

}