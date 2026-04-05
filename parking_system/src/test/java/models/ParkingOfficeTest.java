// File: ICT_4305/Week_4/src/test/java/classes/ParkingOfficeTest.java
package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Address;
import models.Customer;
import models.ParkingOffice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParkingOfficeTest {

    private ParkingOffice parkingOffice;

    @BeforeEach
    void setUp() {
        parkingOffice = new ParkingOffice("University Parking", createTestAddress());
    }

    private Address createTestAddress() {
        Address address = new Address();
        address.setStreetAddress1("123 Main St");
        address.setCity("Springfield");
        address.setState("IL");
        address.setZipCode("62701");
        return address;
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("University Parking", parkingOffice.getName());
        assertNotNull(parkingOffice.getAddress());
        assertEquals("Springfield", parkingOffice.getAddress().getCity());
        assertNotNull(parkingOffice.getCustomers());
        assertNotNull(parkingOffice.getLots());
        assertTrue(parkingOffice.getCustomers().isEmpty());
        assertTrue(parkingOffice.getLots().isEmpty());
    }

    @Test
    void testSetNameAndAddress() {
        parkingOffice.setName("New Name");
        Address a = new Address();
        a.setCity("X");
        parkingOffice.setAddress(a);
        assertEquals("New Name", parkingOffice.getName());
        assertEquals("X", parkingOffice.getAddress().getCity());
    }

    @Test
    void testGetCustomerIdsWhenEmpty() {
        List<UUID> ids = parkingOffice.getCustomerIds();
        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    @Test
    void testGetPermitIdsWhenNoCars() {
        List<UUID> permits = parkingOffice.getPermitIds();
        assertNotNull(permits);
        assertTrue(permits.isEmpty());
    }

    @Test
    void testGetPermitIdsForNullCustomerReturnsEmpty() {
        assertNotNull(parkingOffice.getPermitIds((Customer) null));
        assertTrue(parkingOffice.getPermitIds((Customer) null).isEmpty());
    }

    @Test
    void testGetCustomerWhenNotFound() {
        UUID randomId = UUID.randomUUID();
        assertNull(parkingOffice.getCustomer(randomId));
    }

    @Test
    void testRegisterCustomerDelegatesButDoesNotAddToList() {
        Customer c = new Customer();
        c.setName("Alice");
        c.setAddress(createTestAddress());
        c.setPhoneNumber("555-1234");

        // Current production implementation calls permitManager.register(...) but does not add to the office's customers list.
        parkingOffice.register(c);
        assertTrue(parkingOffice.getCustomers().isEmpty());
    }

    @Test
    void testSetAndGetCustomersAndLotsCollections() {
        // ensure setters work
        parkingOffice.setCustomers(List.of());
        parkingOffice.setLots(List.of());
        assertNotNull(parkingOffice.getCustomers());
        assertNotNull(parkingOffice.getLots());
    }

    @Test
    void testGetCustomerIdsAndPermitIdsWithCustomerCars() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        Car carA = new Car();
        carA.setOwner(customer.getCustomerId());
        carA.setLicense("A1");
        carA.setType(CarType.SUV);
        customer.setCars(List.of(carA));

        parkingOffice.setCustomers(List.of(customer));

        List<UUID> customerIds = parkingOffice.getCustomerIds();
        assertEquals(1, customerIds.size());
        assertTrue(customerIds.contains(customer.getCustomerId()));

        List<UUID> permitIds = parkingOffice.getPermitIds(customer);
        assertEquals(1, permitIds.size());
        assertTrue(permitIds.contains(customer.getCustomerId()));
    }

    @Test
    void testGetCustomerFoundAndEqualsAndHashCode() {
        Customer customer = new Customer();
        UUID customerId = UUID.randomUUID();
        customer.setCustomerId(customerId);
        parkingOffice.setCustomers(List.of(customer));

        assertEquals(customer, parkingOffice.getCustomer(customerId));
        assertEquals(parkingOffice, parkingOffice);
        assertNotEquals(parkingOffice, null);
        assertNotEquals(parkingOffice, "not an office");
    }

    @Test
    void testDefaultConstructorInitializesCollections() {
        ParkingOffice office = new ParkingOffice();
        assertNotNull(office.getCustomers());
        assertNotNull(office.getLots());
        assertTrue(office.getCustomers().isEmpty());
        assertTrue(office.getLots().isEmpty());
    }

    @Test
    void testRegisterCarDelegatesToPermitManagerAndReturnsCar() {
        Customer customer = new Customer();
        UUID customerId = UUID.randomUUID();
        customer.setCustomerId(customerId);
        customer.setCars(new ArrayList<>());
        customer.setName("Alice");
        customer.setAddress(createTestAddress());
        customer.setPhoneNumber("555-1234");

        parkingOffice.setCustomers(List.of(customer));

        Car car = new Car();
        car.setOwner(customerId);
        car.setLicense("ABC-123");
        car.setType(CarType.COMPACT);

        Customer result = parkingOffice.register(car);
        assertNotNull(result);
        assertEquals(customerId, result.getOwner());
        assertEquals(1, customer.getCars().size());
        assertEquals(result, customer.getCars().get(0));
    }

    @Test
    void testParkingOfficeConstructorWithManagersAndNameAddress() {
        ParkingOffice office = new ParkingOffice("Campus", createTestAddress());
        assertEquals("Campus", office.getName());
        assertEquals("Springfield", office.getAddress().getCity());
        assertNotNull(office.getCustomers());
        assertNotNull(office.getLots());
        assertTrue(office.getCustomers().isEmpty());
        assertTrue(office.getLots().isEmpty());
    }
}
