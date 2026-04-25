// File: `ICT_4315/parking_system/src/test/java/strategies/CarTypeStrategyTest.java`
package strategies;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import enums.CarType;
import factories.StrategyFactoryConfig;
import managers.TransactionManager;
import models.Car;
import models.ParkingCharge;
import models.ParkingLot;

class CarTypeStrategyTest {

    private Instant toInstant(int year, int month, int day, int hour, int minute) {
        return StrategyTestHelper.toInstant(year, month, day, hour, minute);
    }

    private Car createCar(CarType type) {
        return StrategyTestHelper.createCar(type);
    }

    private CarTypeStrategy defaultCompactStrategy() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.8)
                .carTypes(java.util.List.of(CarType.COMPACT))
                .build();
        return new CarTypeStrategy(cfg);
    }

    @Test
    void entryDailyCompactShouldUseDiscountedDailyRate() {
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, defaultCompactStrategy());
        TransactionManager manager = StrategyTestHelper.createManager();

        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), toInstant(2026, 4, 18, 10, 0), null);

        assertEquals(16.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void entryDailySuvShouldUseStandardDailyRate() {
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, defaultCompactStrategy());
        TransactionManager manager = StrategyTestHelper.createManager();

        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), toInstant(2026, 4, 18, 10, 0), null);

        assertEquals(20.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void exitHourlyCompactShouldChargeDiscountedHourlyRate() {
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, defaultCompactStrategy());
        TransactionManager manager = StrategyTestHelper.createManager();

        Instant entry = toInstant(2026, 4, 18, 9, 0);
        Instant exit = toInstant(2026, 4, 18, 12, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), entry, exit);

        assertEquals(4.80, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void exitHourlySuvShouldChargeStandardHourlyRate() {
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, defaultCompactStrategy());
        TransactionManager manager = StrategyTestHelper.createManager();

        Instant entry = toInstant(2026, 4, 18, 9, 0);
        Instant exit = toInstant(2026, 4, 18, 11, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), entry, exit);

        assertEquals(4.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void accessorsShouldWork() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.8)
                .carTypes(java.util.List.of(CarType.COMPACT))
                .build();
        CarTypeStrategy s = new CarTypeStrategy(cfg);
        s.setRateModifier(0.85);
        assertEquals(0.85, s.getRateModifier(), 0.0001);
    }
}
