package strategies;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import enums.CarType;
import managers.TransactionManager;
import models.Car;
import models.ParkingCharge;
import models.ParkingLot;

class SpecialDaysStrategyTest {

    private Instant toInstant(int year, int month, int day, int hour, int minute) {
        return StrategyTestHelper.toInstant(year, month, day, hour, minute);
    }

    private Car createCar(CarType type) {
        return StrategyTestHelper.createCar(type);
    }

    @Test
    void specialDayDailyCompactShouldUseDiscount() {
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, new SpecialDaysStrategy());
        TransactionManager manager = new TransactionManager();

        Instant specialDay = toInstant(2026, 4, 25, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), specialDay, null);

        assertEquals(16.00, charge.getAmount().getDollars(), 0.001); // 20 * 0.8
    }

    @Test
    void normalDayDailyCompactShouldUseBaseCompactRate() {
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, new SpecialDaysStrategy());
        TransactionManager manager = new TransactionManager();

        Instant normalDay = toInstant(2026, 4, 18, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), normalDay, null);

        assertEquals(20.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void specialDayHourlySuvShouldApplyDiscountedHourlyRate() {
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, new SpecialDaysStrategy());
        TransactionManager manager = new TransactionManager();

        Instant specialDay = toInstant(2026, 4, 25, 9, 0);
        Instant exit = toInstant(2026, 4, 25, 12, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), specialDay, exit);

        assertEquals(4.80, charge.getAmount().getDollars(), 0.001); // 2 * 3 * 0.8
    }

    @Test
    void accessorsShouldWork() {
        SpecialDaysStrategy s = new SpecialDaysStrategy();
        s.setSpecialDiscount(0.5);
        assertEquals(0.5, s.getSpecialDiscount(), 0.0001);
    }
}