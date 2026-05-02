package observers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import managers.TransactionManager;
import models.Car;
import models.ParkingCharge;
import models.ParkingLot;

public class ParkingObserverTest {

    private TransactionManager tm;
    private ParkingLot lot;
    private Car car;

    @BeforeEach
    public void setup() {
        tm = new TransactionManager();
        lot = new ParkingLot();
        lot.setLotId(java.util.UUID.randomUUID());
        lot.setCapacity(10);
        lot.setChargeOnExit(true);
        lot.setLotFee(new models.Money(5L));
        lot.addObserver(new ParkingObserver(tm));

        java.util.UUID ownerId = java.util.UUID.randomUUID();
        car = new Car(ownerId, "PERMIT-1", java.time.LocalDate.now().plusDays(1), "ABC-123", enums.CarType.COMPACT);
    }

    @Test
    public void testSingleObserverEnter() {
        LocalDateTime now = LocalDateTime.now();
        ParkingCharge charge = lot.park(now, car);
        assertNotNull(charge);
        assertTrue(lot.getParkedCars().contains(car));
    }

    @Test
    public void testEnterThenLeave() {
        LocalDateTime now = LocalDateTime.now();
        ParkingCharge enterCharge = lot.park(now, car);
        assertNotNull(enterCharge);

        LocalDateTime leaveTime = now.plusHours(2);
        ParkingCharge exitCharge = lot.leave(leaveTime, car);
        assertNotNull(exitCharge);
        assertFalse(lot.getParkedCars().contains(car));
    }
}
