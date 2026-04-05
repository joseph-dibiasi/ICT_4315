// File: ICT_4305/Week_4/src/test/java/classes/PermitManagerTest.java
package models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import models.Address;
import models.Car;
import models.CarType;
import models.Customer;
import models.PermitManager;

class PermitManagerTest {

    @Test
    void testRegisterCreatesCustomerWithFields() {
        PermitManager pm = new PermitManager();
        Address addr = new Address();
        addr.setCity("Town");
        Customer c = pm.register("Alice", addr, "555-0000");

        assertNotNull(c);
        assertNotNull(c.getCustomerId());
        assertEquals("Alice", c.getName());
        assertEquals("Town", c.getAddress().getCity());
        assertEquals("555-0000", c.getPhoneNumber());
    }

    @Test
    void testRegisterCustomerAddsCarToCustomerCarsList() {
        PermitManager pm = new PermitManager();
        Customer customer = new Customer();
        UUID ownerId = UUID.randomUUID();
        customer.setCustomerId(ownerId);
        // production code expects getCars() to be non-null before calling register(customer,...)
        customer.setCars(new ArrayList<>());

        Customer returned = pm.register(customer, "ABC-123", CarType.SUV);

        assertSame(customer, returned);
        assertNotNull(customer.getCars());
        assertEquals(1, customer.getCars().size());

        Car registered = customer.getCars().get(0);
        assertEquals(ownerId, registered.getOwner());
        assertEquals("ABC-123", registered.getLicense());
        assertEquals(CarType.SUV, registered.getType());

        LocalDate expectedMin = LocalDate.now();
        LocalDate expectedMax = LocalDate.now().plusYears(2);
        assertTrue(registered.getPermitExpiration().isAfter(expectedMin.minusDays(1)));
        assertTrue(registered.getPermitExpiration().isBefore(expectedMax));
    }

    @Test
    void testRegisterCarProducesOneYearExpiration() {
        PermitManager pm = new PermitManager();
        UUID ownerId = UUID.randomUUID();
        Car car = pm.registerCar(ownerId, "Bob", "ZZZ-999", CarType.COMPACT);

        assertEquals(ownerId, car.getOwner());
        assertEquals("Bob", car.getPermit());
        assertEquals("ZZZ-999", car.getLicense());
        assertEquals(CarType.COMPACT, car.getType());

        LocalDate expected = LocalDate.now().plusYears(1);
        assertEquals(expected.getYear(), car.getPermitExpiration().getYear());
    }
}
