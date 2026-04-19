package strategies;

import java.time.Instant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import enums.CarType;
import managers.TransactionManager;
import models.Car;
import models.ParkingCharge;
import models.ParkingLot;

class CombinedStrategiesTest {

    private Instant toInstant(int year, int month, int day, int hour, int minute) {
        return StrategyTestHelper.toInstant(year, month, day, hour, minute);
    }

    private Car createCar(CarType type) {
        return StrategyTestHelper.createCar(type);
    }

    @Test
    void compactCarOnWeekendShouldGetBothDiscounts() {
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, new CarTypeStrategy(), new DayOfWeekStrategy());
        TransactionManager manager = StrategyTestHelper.createManager();

        Instant saturday = toInstant(2026, 4, 18, 10, 0); // Saturday
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), saturday, null);

        // 20 * 0.8 (compact) * 0.9 (weekend) = 14.4
        assertEquals(14.40, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void suvOnWeekdayShouldUseBaseRate() {
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, new CarTypeStrategy(), new DayOfWeekStrategy());
        TransactionManager manager = StrategyTestHelper.createManager();

        Instant monday = toInstant(2026, 4, 20, 10, 0); // Monday
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), monday, null);

        assertEquals(20.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void compactCarOnWeekdayShouldGetCarTypeDiscountOnly() {
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, new CarTypeStrategy(), new DayOfWeekStrategy());
        TransactionManager manager = StrategyTestHelper.createManager();

        Instant monday = toInstant(2026, 4, 20, 10, 0); // Monday
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), monday, null);

        // 20 * 0.8 = 16
        assertEquals(16.00, charge.getAmount().getDollars(), 0.001);
    }
}