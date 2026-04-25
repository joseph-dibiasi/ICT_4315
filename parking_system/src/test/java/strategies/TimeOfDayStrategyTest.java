// File: `ICT_4315/parking_system/src/test/java/strategies/TimeOfDayStrategyTest.java`
package strategies;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import factories.StrategyFactoryConfig;
import managers.TransactionManager;
import models.Car;
import models.Money;
import models.ParkingCharge;
import models.ParkingLot;

class TimeOfDayStrategyTest {

    private Instant toInstant(int year, int month, int day, int hour, int minute) {
        return StrategyTestHelper.toInstant(year, month, day, hour, minute);
    }

    private Car createCar() {
        return StrategyTestHelper.createCar(null);
    }

    private TimeOfDayStrategy defaultPeakStrategy() {
        Instant start = LocalDateTime.of(1970,1,1,9,0).atZone(ZoneId.systemDefault()).toInstant();
        Instant end = LocalDateTime.of(1970,1,1,17,0).atZone(ZoneId.systemDefault()).toInstant();
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(1.2)
                .timeOfDayRange(new Instant[] { start, end })
                .build();
        return new TimeOfDayStrategy(cfg);
    }

    @Test
    void peakHourlyShouldApplySurcharge() {
        TimeOfDayStrategy strat = defaultPeakStrategy();
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, strat);
        TransactionManager manager = StrategyTestHelper.createManager();

        Instant entry = toInstant(2026, 4, 20, 10, 0);
        Instant exit = toInstant(2026, 4, 20, 11, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(), entry, exit);

        assertEquals(2.40, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void offPeakHourlyShouldUseBaseRate() {
        TimeOfDayStrategy strat = defaultPeakStrategy();
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, strat);
        TransactionManager manager = StrategyTestHelper.createManager();

        Instant entry = toInstant(2026, 4, 20, 20, 0);
        Instant exit = toInstant(2026, 4, 20, 21, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(), entry, exit);

        assertEquals(2.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void accessorsShouldWork() {
        Instant start = LocalDateTime.of(1970,1,1,9,0).atZone(ZoneId.systemDefault()).toInstant();
        Instant end = LocalDateTime.of(1970,1,1,17,0).atZone(ZoneId.systemDefault()).toInstant();
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(1.2)
                .timeOfDayRange(new Instant[] { start, end })
                .build();
        TimeOfDayStrategy s = new TimeOfDayStrategy(cfg);
        s.setPeakSurcharge(3.14);
        assertEquals(3.14, s.getPeakSurcharge(), 0.0001);
    }

    @Test
    void nullTimesReturnUnchanged() {
        Instant start = LocalDateTime.of(1970,1,1,9,0).atZone(ZoneId.systemDefault()).toInstant();
        Instant end = LocalDateTime.of(1970,1,1,17,0).atZone(ZoneId.systemDefault()).toInstant();
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(1.2)
                .timeOfDayRange(new Instant[] { start, end })
                .build();
        TimeOfDayStrategy s = new TimeOfDayStrategy(cfg);
        Money m = new Money(123L);
        Money out = s.adjustCharge(m, null, null, null, null);
        assertEquals(123L, out.getCents());
    }
}
