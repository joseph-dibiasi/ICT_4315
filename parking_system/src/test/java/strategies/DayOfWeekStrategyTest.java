// File: `ICT_4315/parking_system/src/test/java/strategies/DayOfWeekStrategyTest.java`
package strategies;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import decorators.BaseParkingChargeCalculator;
import decorators.DayOfWeekDecorator;
import factories.DecoratorFactoryConfig;
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

    private DayOfWeekDecorator defaultWeekendDecorator() {
        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder()
                .rateModifier(0.9)
                .daysOfWeek(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
                .build();
        return new DayOfWeekDecorator(new BaseParkingChargeCalculator(), cfg);
    }

    @Test
    void weekendDailyRateShouldApplyWeekendDiscount() {
        DayOfWeekDecorator decorator = defaultWeekendDecorator();
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, decorator);
        TransactionManager manager = new TransactionManager();

        Instant saturday = toInstant(2026, 4, 18, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), saturday, null);

        assertEquals(18.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void weekdayDailyRateShouldUseStandardRate() {
        DayOfWeekDecorator decorator = defaultWeekendDecorator();
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, decorator);
        TransactionManager manager = new TransactionManager();

        Instant monday = toInstant(2026, 4, 20, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), monday, null);

        assertEquals(20.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void weekendHourlyRateShouldApplyWeekendDiscount() {
        DayOfWeekDecorator decorator = defaultWeekendDecorator();
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, decorator);
        TransactionManager manager = new TransactionManager();

        Instant saturdayEntry = toInstant(2026, 4, 18, 10, 0);
        Instant saturdayExit = toInstant(2026, 4, 18, 12, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), saturdayEntry, saturdayExit);

        assertEquals(3.60, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void accessorsShouldWork() {
        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder()
                .rateModifier(0.9)
                .daysOfWeek(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
                .build();
        DayOfWeekDecorator s = new DayOfWeekDecorator(new BaseParkingChargeCalculator(), cfg);
        s.setWeekendModifier(0.77);
        assertEquals(0.77, s.getWeekendModifier(), 0.0001);
    }
    
    @Test
    void TimeOfDayWithNullTimesReturnsUnchanged() {
        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder()
                .rateModifier(0.9)
                .daysOfWeek(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
                .build();
        
        DayOfWeekDecorator s = new DayOfWeekDecorator(new BaseParkingChargeCalculator(), cfg);
        Money m = new Money(77L);
        ParkingLot lot = StrategyTestHelper.createLot(false, 0.77, s);
        Money out = s.calculate(lot, null, null, null);
        assertEquals(77L, out.getCents());
    }
}
