package strategies;

import java.time.Instant;
import java.time.ZoneId;

import models.Car;
import models.Money;
import models.ParkingLot;

public class SpecialDaysStrategy implements ParkingStrategy {

    private Double specialDiscount = 0.2; // 20% discount on special days

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