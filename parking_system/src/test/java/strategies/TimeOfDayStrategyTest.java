package strategies;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import managers.TransactionManager;
import models.Car;
import models.ParkingCharge;
import models.ParkingLot;

class TimeOfDayStrategyTest {

    private Instant toInstant(int year, int month, int day, int hour, int minute) {
        return StrategyTestHelper.toInstant(year, month, day, hour, minute);
    }

    private Car createCar() {
        return StrategyTestHelper.createCar(null);
    }

    @Test
    void peakHourlyShouldApplySurcharge() {
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, new TimeOfDayStrategy());
        TransactionManager manager = StrategyTestHelper.createManager();

        Instant entry = toInstant(2026, 4, 20, 10, 0);
        Instant exit = toInstant(2026, 4, 20, 11, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(), entry, exit);

        assertEquals(2.40, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void offPeakHourlyShouldUseBaseRate() {
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, new TimeOfDayStrategy());
        TransactionManager manager = StrategyTestHelper.createManager();

        Instant entry = toInstant(2026, 4, 20, 20, 0);
        Instant exit = toInstant(2026, 4, 20, 21, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(), entry, exit);

        assertEquals(2.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void accessorsShouldWork() {
        TimeOfDayStrategy s = new TimeOfDayStrategy();
        s.setPeakSurcharge(3.14);
        assertEquals(3.14, s.getPeakSurcharge(), 0.0001);
    }
}