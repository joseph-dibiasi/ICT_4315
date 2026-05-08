// File: `ICT_4315/parking_system/src/test/java/strategies/SpecialDaysStrategyTest.java`
package strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import decorators.BaseParkingChargeCalculator;
import decorators.SpecialDaysDecorator;
import factories.DecoratorFactoryConfig;
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

    private SpecialDaysDecorator defaultSpecialDayDecorator() {
        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder()
                .rateModifier(0.8)
                .specialDays(List.of(25))
                .build();
        return new SpecialDaysDecorator(new BaseParkingChargeCalculator(), cfg);
    }

    @Test
    void specialDayDailyCompactShouldUseDiscount() {
        SpecialDaysDecorator decorator = defaultSpecialDayDecorator();
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, decorator);
        TransactionManager manager = new TransactionManager();

        Instant specialDay = toInstant(2026, 4, 25, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), specialDay, null);

        assertEquals(16.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void normalDayDailyCompactShouldUseBaseCompactRate() {
        SpecialDaysDecorator decorator = defaultSpecialDayDecorator();
        ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, decorator);
        TransactionManager manager = new TransactionManager();

        Instant normalDay = toInstant(2026, 4, 18, 10, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.COMPACT), normalDay, null);

        assertEquals(20.00, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void specialDayHourlySuvShouldApplyDiscountedHourlyRate() {
        SpecialDaysDecorator decorator = defaultSpecialDayDecorator();
        ParkingLot lot = StrategyTestHelper.createLot(true, 2.0, decorator);
        TransactionManager manager = new TransactionManager();

        Instant specialDay = toInstant(2026, 4, 25, 9, 0);
        Instant exit = toInstant(2026, 4, 25, 12, 0);
        ParkingCharge charge = manager.calculateParkingCharge(lot, createCar(CarType.SUV), specialDay, exit);

        assertEquals(4.80, charge.getAmount().getDollars(), 0.001);
    }

    @Test
    void accessorsShouldWork() {
        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder()
                .rateModifier(0.8)
                .specialDays(List.of(25))
                .build();
        SpecialDaysDecorator s = new SpecialDaysDecorator(new BaseParkingChargeCalculator(), cfg);
        s.setSpecialDiscount(0.5);
        assertEquals(0.5, s.getSpecialDiscount(), 0.0001);
    }

    @Test
    void specialDaysUsesExitTimeWhenEntryNull() {
        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder()
                .rateModifier(0.8)
                .specialDays(List.of(25))
                .build();
        Instant exit = toInstant(2026,4,25,12,0);
        SpecialDaysDecorator s = new SpecialDaysDecorator(new BaseParkingChargeCalculator(), cfg);
        ParkingLot lot = StrategyTestHelper.createLot(false, 1.0, s);
        Money out = s.calculate(lot, null, null, exit);
        assertEquals(80L, out.getCents());
    }

    @Test
    void specialDaysWithEmptyListDoesNotModify() {
        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().specialDays(List.of()).rateModifier(0.5).build();
        SpecialDaysDecorator s = new SpecialDaysDecorator(new BaseParkingChargeCalculator(), cfg);
        Instant any = toInstant(2026,4,25,12,0);
        ParkingLot lot = StrategyTestHelper.createLot(false, 1.0, s);
        Money out = s.calculate(lot, null, any, null);
        assertEquals(100L, out.getCents());
    }

    @Test
    void specialDaysWithNullTimesReturnsUnchanged() {
        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder()
                .rateModifier(0.8)
                .specialDays(List.of(25))
                .build();
        SpecialDaysDecorator s = new SpecialDaysDecorator(new BaseParkingChargeCalculator(), cfg);
        ParkingLot lot = StrategyTestHelper.createLot(false, 0.77, s);
        Money out = s.calculate(lot, null, null, null);
        assertEquals(77L, out.getCents());
    }
}
