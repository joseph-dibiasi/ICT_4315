package decorators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import models.Car;
import models.Money;
import models.ParkingLot;

class ParkingChargeDecoratorTest {

    private static class TestDecorator extends ParkingChargeDecorator {
        TestDecorator(ParkingChargeCalculator wrappedCalculator) {
            super(wrappedCalculator);
        }

        @Override
        public Money calculate(ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
            return wrappedCalculator.calculate(parkingLot, car, entryTime, exitTime);
        }
    }

    @Test
    void constructorRejectsNullWrappedCalculator() {
        assertThrows(IllegalArgumentException.class, () -> new TestDecorator(null));
    }

    @Test
    void calculateCanDelegateToWrappedCalculator() {
        ParkingChargeCalculator wrapped = new BaseParkingChargeCalculator();
        TestDecorator decorator = new TestDecorator(wrapped);

        ParkingLot lot = new ParkingLot();
        lot.setChargeOnExit(false);
        lot.setLotFee(new Money(2.5));

        Money amount = decorator.calculate(lot, new Car(), Instant.now(), null);
        assertEquals(250L, amount.getCents());
    }
}
