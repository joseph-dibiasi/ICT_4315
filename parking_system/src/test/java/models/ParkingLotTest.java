package models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import factories.StrategyFactoryConfig;
import models.ParkingEvent.EventType;
import strategies.DayOfWeekStrategy;

class ParkingLotTest {

    @Test
    void testSetAndGetLotId() {
        ParkingLot lot = new ParkingLot();
        UUID id = UUID.randomUUID();
        lot.setLotId(id);
        assertEquals(id, lot.getLotId());
    }

    @Test
    void testSetAndGetAddress() {
        ParkingLot lot = new ParkingLot();
        Address address = Address.builder().build();
        address.setCity("New York");
        lot.setAddress(address);
        assertEquals(address, lot.getAddress());
        assertEquals("New York", lot.getAddress().getCity());
    }

    @Test
    void testSetAndGetCapacity() {
        ParkingLot lot = new ParkingLot();
        lot.setCapacity(100);
        assertEquals(100, lot.getCapacity());
    }

    @Test
    void testSetAndGetChargeOnExit() {
        ParkingLot lot = new ParkingLot();
        lot.setChargeOnExit(true);
        assertTrue(lot.getChargeOnExit());
    }

    @Test
    void testSetAndGetLotFee() {
        ParkingLot lot = new ParkingLot();
        Money fee = new Money(1500L); // $15.00
        lot.setLotFee(fee);
        assertEquals(1500L, lot.getLotFee().getCents());
    }

    @Test
    void testSetAndGetParkedCars() {
        ParkingLot lot = new ParkingLot();
        Set<Car> cars = new HashSet<>();

        Car car = new Car();
        car.setLicense("XYZ-123");
        cars.add(car);

        lot.setParkedCars(cars);

        assertEquals(1, lot.getParkedCars().size());
        assertTrue(lot.getParkedCars().contains(car));
    }

    @Test
    void testGetParkedCarsInitializesIfNull() {
        ParkingLot lot = new ParkingLot();
        assertNotNull(lot.getParkedCars());
        assertTrue(lot.getParkedCars().isEmpty());
    }

    @Test
    void testToString() {
        ParkingLot lot = new ParkingLot();
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        lot.setLotId(id);

        Address address = Address.builder().build();
        address.setStreetAddress1("123 Main St");
        address.setCity("Gotham");
        address.setState("NJ");
        address.setZipCode("07001");
        lot.setAddress(address);

        lot.setCapacity(200);

        String result = lot.toString();

        assertTrue(result.contains("lotId=11111111-1111-1111-1111-111111111111"));
        assertTrue(result.contains("address="));
        assertTrue(result.contains("capacity=200"));
    }

    @Test
    void testEqualsAndHashCode() {
        ParkingLot lot1 = new ParkingLot();
        ParkingLot lot2 = new ParkingLot();
        UUID id = UUID.randomUUID();
        lot1.setLotId(id);
        lot2.setLotId(id);
        lot1.setCapacity(50);
        lot2.setCapacity(50);
        lot1.setChargeOnExit(true);
        lot2.setChargeOnExit(true);
        Money fee = new Money(100L);
        lot1.setLotFee(fee);
        lot2.setLotFee(fee);
        lot1.setParkedCars(new HashSet<>());
        lot2.setParkedCars(new HashSet<>());

        assertEquals(lot1, lot2);
        assertEquals(lot1.hashCode(), lot2.hashCode());
        assertNotEquals(lot1, null);
        assertNotEquals(lot1, "not a lot");
    }

    @Test
    void testNotEqualsDifferentParkedCars() {
        ParkingLot lot1 = new ParkingLot();
        ParkingLot lot2 = new ParkingLot();
        UUID id = UUID.randomUUID();
        lot1.setLotId(id);
        lot2.setLotId(id);
        lot1.setParkedCars(new HashSet<>());
        lot2.setParkedCars(new HashSet<>());

        Car car = new Car();
        car.setLicense("ABC");
        lot2.getParkedCars().add(car);

        assertNotEquals(lot1, lot2);
    }

    @Test
    void testNotEqualsNullAddress() {
        ParkingLot lot1 = new ParkingLot();
        lot1.setAddress(Address.builder().build());

        ParkingLot lot2 = new ParkingLot();
        // address remains null

        assertNotEquals(lot1, lot2);
    }

    @Test
    void testNotEqualsNullAddressReversed() {
        ParkingLot lot1 = new ParkingLot();
        // address null

        ParkingLot lot2 = new ParkingLot();
        lot2.setAddress(Address.builder().build());

        assertNotEquals(lot1, lot2);
    }

    @Test
    void testNotEqualsNullLotId() {
        ParkingLot lot1 = new ParkingLot();
        lot1.setLotId(UUID.randomUUID());

        ParkingLot lot2 = new ParkingLot();
        // lotId remains null

        assertNotEquals(lot1, lot2);
    }

    @Test
    void testNotEqualsNullLotFee() {
        ParkingLot lot1 = new ParkingLot();
        lot1.setLotFee(new Money(100L));

        ParkingLot lot2 = new ParkingLot();
        // lotFee remains null

        assertNotEquals(lot1, lot2);
    }
    
    @Test
    public void testEqualsSameObject() {
        ParkingLot lot = new ParkingLot();
        assertTrue(lot.equals(lot));
    }
    

    @Test
    void parkingLotSetStrategiesAndEquals() {
        ParkingLot p1 = new ParkingLot();
        ParkingLot p2 = new ParkingLot();
        p1.setLotId(UUID.randomUUID());
        p2.setLotId(p1.getLotId());
        p1.setCapacity(10);
        p2.setCapacity(10);
        p1.setChargeOnExit(false);
        p2.setChargeOnExit(false);
        p1.setLotFee(new Money(100L));
        p2.setLotFee(new Money(100L));

        assertTrue(p1.equals(p2));

        List<strategies.ParkingStrategy> strategies = new ArrayList<>();
        strategies.add(defaultWeekendStrategy());
        p1.setStrategies(strategies);

        assertFalse(p1.equals(p2));
    }
    
    private DayOfWeekStrategy defaultWeekendStrategy() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.9)
                .daysOfWeek(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
                .build();
        return new DayOfWeekStrategy(cfg);
    }
    
    @Test
    public void notifyObserversThrowsWhenNoneRegistered() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        ParkingEvent event = new ParkingEvent(EventType.ENTRY, LocalDateTime.now(), lot, car);
        assertThrows(IllegalArgumentException.class, () -> lot.notifyObservers(event));
    }

    @Test
    public void addObserver_notify_then_removeObserver_behaviour() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("P");
        car.setPermitExpiration(LocalDate.now().plusDays(1));

        observers.ParkingAction obs = new observers.ParkingAction() {
            @Override
            public ParkingCharge update(ParkingEvent event) {
                return ParkingCharge.builder(car.getOwner(), UUID.randomUUID()).amount(new Money(500L)).build();
            }
        };

        // add observer and ensure notifyObservers returns a charge
        lot.addObserver(obs);
        ParkingEvent event = new ParkingEvent(EventType.ENTRY, LocalDateTime.now(), lot, car);
        ParkingCharge returned = lot.notifyObservers(event);
        assertNotNull(returned);

        // remove observer and notifyObservers should now return null (no observers producing a charge)
        lot.removeObserver(obs);
        // observers list remains but no one returns a charge -> notifyObservers returns null
        ParkingCharge afterRemove = lot.notifyObservers(event);
        assertNull(afterRemove);
    }

    @Test
    public void park_withObserverReturningNull_addsCarLocally() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("X");
        car.setPermitExpiration(LocalDate.now().plusDays(1));

        // observer that does nothing (returns null)
        observers.ParkingAction noop = new observers.ParkingAction() {
            @Override
            public ParkingCharge update(ParkingEvent event) {
                return null;
            }
        };

        lot.addObserver(noop);
        ParkingCharge charge = lot.park(LocalDateTime.now(), car);
        assertNull(charge);
        assertTrue(lot.getParkedCars().contains(car));
    }

    @Test
    public void park_nullCar_throws() {
        ParkingLot lot = new ParkingLot();
        assertThrows(IllegalArgumentException.class, () -> lot.park((Car) null));
    }

    @Test
    public void removeObserver_noObservers_doesNotThrow() {
        ParkingLot lot = new ParkingLot();
        // initial observers list is null; removing should be a no-op
        lot.removeObserver(null);
    }

    @Test
    public void addObserver_null_doesNotInitializeObservers() {
        ParkingLot lot = new ParkingLot();
        lot.addObserver(null);
        Car car = new Car();
        ParkingEvent event = new ParkingEvent(EventType.ENTRY, LocalDateTime.now(), lot, car);
        assertThrows(IllegalArgumentException.class, () -> lot.notifyObservers(event));
    }

    @Test
    public void multipleObservers_firstNull_thenSecondReturnsCharge() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("M");
        car.setPermitExpiration(LocalDate.now().plusDays(1));

        observers.ParkingAction first = (event) -> null;
        observers.ParkingAction second = (event) -> ParkingCharge.builder(car.getOwner(), UUID.randomUUID()).amount(new Money(700L)).build();

        lot.addObserver(first);
        lot.addObserver(second);

        ParkingCharge charge = lot.notifyObservers(new ParkingEvent(EventType.ENTRY, LocalDateTime.now(), lot, car));
        assertNotNull(charge);
        assertEquals(700L, charge.getAmount().getCents());
    }

    @Test
    public void park_withNullDate_throws() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        assertThrows(IllegalArgumentException.class, () -> lot.park((LocalDateTime) null, car));
    }
    
    
    @Test
    public void park_withNullCar_throws() {
        ParkingLot lot = new ParkingLot();
        assertThrows(IllegalArgumentException.class, () -> lot.park(LocalDateTime.now(), null));
    }

    @Test
    public void park_withDate_andObserverReturningCharge_doesNotAddCar() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("D");
        car.setPermitExpiration(LocalDate.now().plusDays(1));

        observers.ParkingAction obs = (event) -> ParkingCharge.builder(car.getOwner(), UUID.randomUUID()).amount(new Money(800L)).build();
        lot.addObserver(obs);

        LocalDateTime dt = LocalDateTime.now();
        ParkingCharge ch = lot.park(dt, car);
        assertNotNull(ch);
        assertFalse(lot.getParkedCars().contains(car));
    }

    @Test
    public void leave_withObserverReturningNull_removesCarLocally() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("L");
        car.setPermitExpiration(LocalDate.now().plusDays(1));

        // ensure car is parked locally
        lot.getParkedCars().add(car);

        observers.ParkingAction noop = (event) -> null;
        lot.addObserver(noop);

        ParkingCharge out = lot.leave(LocalDateTime.now(), car);
        assertNull(out);
        assertFalse(lot.getParkedCars().contains(car));
    }
    
    @Test
    public void leave_withNullCar_throws() {
        ParkingLot lot = new ParkingLot();
        assertThrows(IllegalArgumentException.class, () -> lot.leave(LocalDateTime.now(), null));
    }


    @Test
    public void multipleObservers_firstReturnsCharge_secondNotCalled() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("S");
        car.setPermitExpiration(LocalDate.now().plusDays(1));

        observers.ParkingAction first = (event) -> ParkingCharge.builder(car.getOwner(), UUID.randomUUID()).amount(new Money(900L)).build();
        observers.ParkingAction second = (event) -> { throw new AssertionError("Second observer should not be called"); };

        lot.addObserver(first);
        lot.addObserver(second);

        ParkingCharge charge = lot.notifyObservers(new ParkingEvent(EventType.ENTRY, LocalDateTime.now(), lot, car));
        assertNotNull(charge);
        assertEquals(900L, charge.getAmount().getCents());
    }

    @Test
    public void leave_withObserverReturningCharge_returnsCharge() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("LC");
        car.setPermitExpiration(LocalDate.now().plusDays(1));

        observers.ParkingAction obs = (event) -> ParkingCharge.builder(car.getOwner(), UUID.randomUUID()).amount(new Money(1000L)).build();
        lot.addObserver(obs);

        ParkingCharge out = lot.leave(LocalDateTime.now(), car);
        assertNotNull(out);
        assertEquals(1000L, out.getAmount().getCents());
    }

    @Test
    public void leave_withNullDate_andObserverReturningNull_removesCarLocally() {
        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("NULLD");
        car.setPermitExpiration(LocalDate.now().plusDays(1));

        // ensure car is parked locally
        lot.getParkedCars().add(car);

        observers.ParkingAction noop = (event) -> null;
        lot.addObserver(noop);

        ParkingCharge out = lot.leave(null, car);
        assertNull(out);
        assertFalse(lot.getParkedCars().contains(car));
    }

}
