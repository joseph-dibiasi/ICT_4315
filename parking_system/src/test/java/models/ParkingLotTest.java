package models;

import org.junit.jupiter.api.Test;

import models.Address;
import models.Car;
import models.Money;
import models.ParkingLot;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        Address address = new Address();
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

        Address address = new Address();
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
        lot1.setAddress(new Address());

        ParkingLot lot2 = new ParkingLot();
        // address remains null

        assertNotEquals(lot1, lot2);
    }

    @Test
    void testNotEqualsNullAddressReversed() {
        ParkingLot lot1 = new ParkingLot();
        // address null

        ParkingLot lot2 = new ParkingLot();
        lot2.setAddress(new Address());

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

}
