package strategies;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import models.Car;
import models.Money;
import models.ParkingLot;

public class TimeOfDayStrategy implements ParkingStrategy {

    private Double peakSurcharge = 0.2; // 20% surcharge during peak
    private Double specialDiscount = 0.2; // 20% discount on special days

    @Override
    public Money adjustCharge(Money currentCharge, ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
        Instant time = entryTime != null ? entryTime : exitTime;
        if (time == null) return currentCharge;

        double multiplier = 1.0;
        if (isPeakTime(time)) {
            multiplier += peakSurcharge;
        }
        if (isSpecialDay(time)) {
            multiplier -= specialDiscount;
        }
        return new Money(currentCharge.getDollars() * multiplier);
    }

    private boolean isPeakTime(Instant time) {
        LocalTime localTime = time.atZone(ZoneId.systemDefault()).toLocalTime();
        return localTime.isAfter(LocalTime.of(8, 0)) && localTime.isBefore(LocalTime.of(18, 0));
    }

    private boolean isSpecialDay(Instant time) {
        int day = time.atZone(ZoneId.systemDefault()).getDayOfMonth();
        return day == 25;
    }

    public Double getPeakSurcharge() {
        return peakSurcharge;
    }

    public void setPeakSurcharge(Double peakSurcharge) {
        this.peakSurcharge = peakSurcharge;
    }

    public Double getSpecialDiscount() {
        return specialDiscount;
    }

    public void setSpecialDiscount(Double specialDiscount) {
        this.specialDiscount = specialDiscount;
    }
}