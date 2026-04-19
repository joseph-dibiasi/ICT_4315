package strategies;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import enums.CarType;
import managers.TransactionManager;
import models.Car;
import models.Money;
import models.ParkingLot;

public final class StrategyTestHelper {

    private StrategyTestHelper() {
        // utility class
    }

    public static Instant toInstant(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(ZoneId.systemDefault()).toInstant();
    }

    public static Car createCar(CarType type) {
        return new Car(UUID.randomUUID(), "PERMIT-" + UUID.randomUUID(), LocalDate.now().plusYears(1), "PLATE-" + UUID.randomUUID().toString().substring(0, 5), type);
    }

    public static ParkingLot createLot(boolean chargeOnExit, double fee, ParkingStrategy... strategies) {
        ParkingLot lot = new ParkingLot();
        lot.setChargeOnExit(chargeOnExit);
        lot.setLotFee(new Money(fee));
        for (ParkingStrategy strategy : strategies) {
            lot.getStrategies().add(strategy);
        }
        return lot;
    }

    public static TransactionManager createManager() {
        return new TransactionManager();
    }
}
