package strategies;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;

import models.Car;
import models.Money;
import models.ParkingLot;

public class DayOfWeekStrategy implements ParkingStrategy {

    private Double weekendModifier = 0.9;

    @Override
    public Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Instant time = entryTime != null ? entryTime : exitTime;
        if (time != null) {
            DayOfWeek day = time.atZone(ZoneId.systemDefault()).getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                return new Money(currentCharge.getDollars() * weekendModifier);
            }
        }
        return currentCharge;
    }

    public Double getWeekendModifier() {
        return weekendModifier;
    }

    public void setWeekendModifier(Double weekendModifier) {
        this.weekendModifier = weekendModifier;
    }
}