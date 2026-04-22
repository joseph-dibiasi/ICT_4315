// File: ICT_4305/Week_4/src/test/java/classes/CustomerTest.java
package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void testSetAndGetCustomerId() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder(id, "Test").build();
        assertEquals(id, customer.getCustomerId());
    }

    @Test
    void testSetAndGetNameAndPhone() {
        Customer customer = Customer.builder(UUID.randomUUID(), "Test").build();
        customer.setName("Alice");
        customer.setPhoneNumber("123-456-7890");
        assertEquals("Alice", customer.getName());
        assertEquals("123-456-7890", customer.getPhoneNumber());
    }

    @Test
    void testSetAndGetAddress() {
        Customer customer = Customer.builder(UUID.randomUUID(), "Test").build();
        Address address = Address.builder().build();
        address.setStreetAddress1("100 Test St");
        address.setCity("CityX");
        customer.setAddress(address);
        assertEquals(address, customer.getAddress());
        assertEquals("100 Test St", customer.getAddress().getStreetAddress1());
    }

    @Test
    void testSetAndGetCarsList() {
        Customer customer = Customer.builder(UUID.randomUUID(), "Test").build();
        List<Car> cars = new ArrayList<>();
        Car car = new Car();
        car.setLicense("XYZ-123");
        cars.add(car);

        customer.setCars(cars);
        assertEquals(cars, customer.getCars());
        assertEquals(1, customer.getCars().size());
        assertEquals(car, customer.getCars().get(0));
    }

    @Test
    void testToStringContainsNameAndAddressInfo() {
        Customer customer = Customer.builder(UUID.randomUUID(), "Test").build();
        customer.setCustomerId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        customer.setName("Jane Doe");

        Address address = Address.builder().build();
        address.setStreetAddress1("789 Oak Ave");
        address.setStreetAddress2("");
        address.setCity("Gotham");
        address.setState("NJ");
        address.setZipCode("07097");

        customer.setAddress(address);
        customer.setPhoneNumber("999-999-9999");

        String toStringResult = customer.toString();
        assertTrue(toStringResult.contains("Jane Doe"));
        // ensure address info appears via toString call
        assertTrue(toStringResult.contains("Gotham"));
        assertTrue(toStringResult.contains("999-999-9999"));
    }

    @Test
    void testEqualsAndHashCodeConsistency() {
        Customer c1 = Customer.builder(UUID.randomUUID(), "Test").build();
        Customer c2 = Customer.builder(UUID.randomUUID(), "Test").build();

        UUID id = UUID.randomUUID();
        c1.setCustomerId(id);
        c2.setCustomerId(id);

        c1.setName("Sam");
        c2.setName("Sam");

        Address address = Address.builder().build();
        address.setCity("A");
        c1.setAddress(address);
        c2.setAddress(address);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        c2.setName("Different");
        assertNotEquals(c1, c2);
    }

    @Test
    void testEqualsSameObjectAndDifferentTypeOrNull() {
        Customer c1 = Customer.builder(UUID.randomUUID(), "Test").build();
        c1.setName("Sam");
        assertEquals(c1, c1);
        assertNotEquals(c1, null);
        assertNotEquals(c1, "not a customer");
    }
}
