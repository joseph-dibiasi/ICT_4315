package decorators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import models.Car;
import models.Money;
import models.ParkingLot;

class BaseParkingChargeCalculatorTest {

    private ParkingLot createLot(boolean chargeOnExit, double fee) {
        ParkingLot lot = new ParkingLot();
        lot.setChargeOnExit(chargeOnExit);
        lot.setLotFee(new Money(fee));
        return lot;
    }

    @Test
    void entryForHourlyLotReturnsZero() {
        BaseParkingChargeCalculator calculator = new BaseParkingChargeCalculator();
        Money amount = calculator.calculate(createLot(true, 5.0), new Car(), Instant.now(), null);
        assertEquals(0L, amount.getCents());
    }

    @Test
    void entryForDailyLotReturnsLotFee() {
        BaseParkingChargeCalculator calculator = new BaseParkingChargeCalculator();
        Money amount = calculator.calculate(createLot(false, 5.0), new Car(), Instant.now(), null);
        assertEquals(500L, amount.getCents());
    }

    @Test
    void exitForHourlyLotUsesMinimumOneHour() {
        BaseParkingChargeCalculator calculator = new BaseParkingChargeCalculator();
        Instant entry = Instant.now();
        Instant exit = entry.plusSeconds(15 * 60);
        Money amount = calculator.calculate(createLot(true, 5.0), new Car(), entry, exit);
        assertEquals(500L, amount.getCents());
    }

    @Test
    void exitForDailyLotReturnsLotFee() {
        BaseParkingChargeCalculator calculator = new BaseParkingChargeCalculator();
        Instant entry = Instant.now();
        Instant exit = entry.plusSeconds(2 * 60 * 60);
        Money amount = calculator.calculate(createLot(false, 5.0), new Car(), entry, exit);
        assertEquals(500L, amount.getCents());
    }
}
