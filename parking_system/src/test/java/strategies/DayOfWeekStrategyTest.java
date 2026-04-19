package strategies;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import enums.CarType;
import managers.TransactionManager;
import models.Car;
import models.ParkingCharge;
import models.ParkingLot;

class DayOfWeekStrategyTest {

    private Instant toInstant(int year, int month, int day, int hour, int minute) {
        return StrategyTestHelper.toInstant(year, month, day, hour, minute);
    }

    private Car createCar(CarType type) {
        return StrategyTestHelper.createCar(type);
    }

    @Test
    void weekendDailyRateShouldApplyWeekendDiscount() {
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, new DayOfWeekStrategy());
        TransactionManager manager = new TransactionManager();

        Instant saturday = toInstant(2026, 4, 18, 10, 0); // 2026-04-18 is a Saturday
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), saturday, null);

        assertEquals(18.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void weekdayDailyRateShouldUseStandardRate() {
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, new DayOfWeekStrategy());
        TransactionManager manager = new TransactionManager();

        Instant monday = toInstant(2026, 4, 20, 10, 0); // 2026-04-20 is a Monday
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), monday, null);

        assertEquals(20.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void weekendHourlyRateShouldApplyWeekendDiscount() {
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, new DayOfWeekStrategy());
        TransactionManager manager = new TransactionManager();

        Instant saturdayEntry = toInstant(2026, 4, 18, 10, 0);
        Instant saturdayExit = toInstant(2026, 4, 18, 12, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), saturdayEntry, saturdayExit);

        assertEquals(3.60, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void accessorsShouldWork() {
        DayOfWeekStrategy s = new DayOfWeekStrategy();
        s.setWeekendModifier(0.77);
        assertEquals(0.77, s.getWeekendModifier(), 0.0001);
    }
}