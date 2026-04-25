// File: `ICT_4315/parking_system/src/test/java/strategies/DayOfWeekStrategyTest.java`
package strategies;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import factories.StrategyFactoryConfig;
import enums.CarType;
import managers.TransactionManager;
import models.Car;
import models.Money;
import models.ParkingCharge;
import models.ParkingLot;

class DayOfWeekStrategyTest {

    private Instant toInstant(int year, int month, int day, int hour, int minute) {
        return StrategyTestHelper.toInstant(year, month, day, hour, minute);
    }

    private Car createCar(CarType type) {
        return StrategyTestHelper.createCar(type);
    }

    private DayOfWeekStrategy defaultWeekendStrategy() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.9)
                .daysOfWeek(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
                .build();
        return new DayOfWeekStrategy(cfg);
    }

    @Test
    void weekendDailyRateShouldApplyWeekendDiscount() {
        DayOfWeekStrategy strat = defaultWeekendStrategy();
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, strat);
        TransactionManager manager = new TransactionManager();

        Instant saturday = toInstant(2026, 4, 18, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), saturday, null);

        assertEquals(18.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void weekdayDailyRateShouldUseStandardRate() {
        DayOfWeekStrategy strat = defaultWeekendStrategy();
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, strat);
        TransactionManager manager = new TransactionManager();

        Instant monday = toInstant(2026, 4, 20, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), monday, null);

        assertEquals(20.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void weekendHourlyRateShouldApplyWeekendDiscount() {
        DayOfWeekStrategy strat = defaultWeekendStrategy();
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, strat);
        TransactionManager manager = new TransactionManager();

        Instant saturdayEntry = toInstant(2026, 4, 18, 10, 0);
        Instant saturdayExit = toInstant(2026, 4, 18, 12, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), saturdayEntry, saturdayExit);

        assertEquals(3.60, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void accessorsShouldWork() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.9)
                .daysOfWeek(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
                .build();
        DayOfWeekStrategy s = new DayOfWeekStrategy(cfg);
        s.setWeekendModifier(0.77);
        assertEquals(0.77, s.getWeekendModifier(), 0.0001);
    }
    
    @Test
    void TimeOfDayWithNullTimesReturnsUnchanged() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.9)
                .daysOfWeek(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
                .build();
        
        DayOfWeekStrategy s = new DayOfWeekStrategy(cfg);
        Money m = new Money(77L);
        Money out = s.adjustCharge(m, null, null, null, null);
        assertEquals(77L, out.getCents());
    }
}
