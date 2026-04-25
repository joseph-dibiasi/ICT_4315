// File: `ICT_4315/parking_system/src/test/java/strategies/SpecialDaysStrategyTest.java`
package strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import factories.StrategyFactoryConfig;
import enums.CarType;
import managers.TransactionManager;
import models.Car;
import models.Money;
import models.ParkingCharge;
import models.ParkingLot;

class SpecialDaysStrategyTest {

    private Instant toInstant(int year, int month, int day, int hour, int minute) {
        return StrategyTestHelper.toInstant(year, month, day, hour, minute);
    }

    private Car createCar(CarType type) {
        return StrategyTestHelper.createCar(type);
    }

    private SpecialDaysStrategy defaultSpecialDayStrategy() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.8)
                .specialDays(List.of(25))
                .build();
        return new SpecialDaysStrategy(cfg);
    }

    @Test
    void specialDayDailyCompactShouldUseDiscount() {
        SpecialDaysStrategy strat = defaultSpecialDayStrategy();
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, strat);
        TransactionManager manager = new TransactionManager();

        Instant specialDay = toInstant(2026, 4, 25, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), specialDay, null);

        assertEquals(16.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void normalDayDailyCompactShouldUseBaseCompactRate() {
        SpecialDaysStrategy strat = defaultSpecialDayStrategy();
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, strat);
        TransactionManager manager = new TransactionManager();

        Instant normalDay = toInstant(2026, 4, 18, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), normalDay, null);

        assertEquals(20.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void specialDayHourlySuvShouldApplyDiscountedHourlyRate() {
        SpecialDaysStrategy strat = defaultSpecialDayStrategy();
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, strat);
        TransactionManager manager = new TransactionManager();

        Instant specialDay = toInstant(2026, 4, 25, 9, 0);
        Instant exit = toInstant(2026, 4, 25, 12, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), specialDay, exit);

        assertEquals(4.80, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void accessorsShouldWork() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.8)
                .specialDays(List.of(25))
                .build();
        SpecialDaysStrategy s = new SpecialDaysStrategy(cfg);
        s.setSpecialDiscount(0.5);
        assertEquals(0.5, s.getSpecialDiscount(), 0.0001);
    }

    @Test
    void specialDaysUsesExitTimeWhenEntryNull() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.8)
                .specialDays(List.of(25))
                .build();
        Instant exit = toInstant(2026,4,25,12,0);
        SpecialDaysStrategy s = new SpecialDaysStrategy(cfg);
        Money m = new Money(100L);
        Money out = s.adjustCharge(m, null, null, null, exit);
        assertEquals(80L, out.getCents());
    }

    @Test
    void specialDaysWithEmptyListDoesNotModify() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().specialDays(List.of()).rateModifier(0.5).build();
        SpecialDaysStrategy s = new SpecialDaysStrategy(cfg);
        Instant any = toInstant(2026,4,25,12,0);
        Money m = new Money(100L);
        Money out = s.adjustCharge(m, null, null, any, null);
        assertEquals(100L, out.getCents());
    }

    @Test
    void specialDaysWithNullTimesReturnsUnchanged() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.8)
                .specialDays(List.of(25))
                .build();
        SpecialDaysStrategy s = new SpecialDaysStrategy(cfg);
        Money m = new Money(77L);
        Money out = s.adjustCharge(m, null, null, null, null);
        assertEquals(77L, out.getCents());
    }
}
