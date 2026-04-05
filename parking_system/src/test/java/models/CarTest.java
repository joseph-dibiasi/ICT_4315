// File: ICT_4305/Week_4/src/test/java/classes/CarTest.java
package models;

import org.junit.jupiter.api.Test;

import models.Car;
import models.CarType;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    void testDefaultConstructor() {
        Car car = new Car();
        assertNull(car.getPermit());
        assertNull(car.getPermitExpiration());
        assertNull(car.getLicense());
        assertNull(car.getType());
        assertNull(car.getOwner());
    }

    @Test
    void testParameterizedConstructor() {
        UUID owner = UUID.randomUUID();
        String permit = "John Doe";
        LocalDate expiration = LocalDate.of(2025, 12, 31);
        String license = "ABC-1234";
        CarType type = CarType.SUV;

        // Note: Car constructor parameter order in production code is (UUID owner, String permit, LocalDate permitExpiration, String license, CarType type)
        Car car = new Car(owner, permit, expiration, license, type);

        assertEquals(permit, car.getPermit());
        assertEquals(expiration, car.getPermitExpiration());
        assertEquals(license, car.getLicense());
        assertEquals(type, car.getType());
        assertEquals(owner, car.getOwner());
    }

    @Test
    void testSetAndGetters() {
        Car car = new Car();
        car.setPermit("Alice");
        assertEquals("Alice", car.getPermit());

        LocalDate date = LocalDate.of(2030, 1, 1);
        car.setPermitExpiration(date);
        assertEquals(date, car.getPermitExpiration());

        car.setLicense("XYZ-7890");
        assertEquals("XYZ-7890", car.getLicense());

        car.setType(CarType.COMPACT);
        assertEquals(CarType.COMPACT, car.getType());

        UUID ownerId = UUID.randomUUID();
        car.setOwner(ownerId);
        assertEquals(ownerId, car.getOwner());
    }

    @Test
    void testToStringAndEquality() {
        UUID owner = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
        Car car = new Car(owner, "Bob", LocalDate.of(2026, 6, 15), "LMN-4567", CarType.SUV);
        String expected = "Car [permit=Bob, permitExpiration=2026-06-15, license=LMN-4567, type=SUV, owner=123e4567-e89b-12d3-a456-556642440000]";
        assertEquals(expected, car.toString());

        Car car2 = new Car(owner, "Bob", LocalDate.of(2026, 6, 15), "LMN-4567", CarType.SUV);
        assertEquals(car, car2);
        assertEquals(car.hashCode(), car2.hashCode());

        // change one field -> not equal
        car2.setLicense("DIFF-1");
        assertNotEquals(car, car2);
    }

    @Test
    void testEqualsAndHashCodeDifferentTypeOrNull() {
        Car car = new Car();
        car.setLicense("XYZ");

        assertNotEquals(car, null);
        assertNotEquals(car, "not a car");
    }
}
