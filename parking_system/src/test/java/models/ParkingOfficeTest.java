// File: ICT_4305/Week_4/src/test/java/classes/ParkingOfficeTest.java
package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.Address;
import models.Car;
import models.CarType;
import models.Customer;
import models.Money;
import models.ParkingCharge;
import models.ParkingLot;
import models.ParkingOffice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void testConstructorWithManagers() {
        PermitManager pm = new PermitManager();
        TransactionManager tm = new TransactionManager();
        ParkingOffice office = new ParkingOffice(pm, tm);
        assertNotNull(office.getCustomers());
        assertNotNull(office.getLots());
        assertTrue(office.getCustomers().isEmpty());
        assertTrue(office.getLots().isEmpty());
    }

    @Test
    void testConstructorWithManagersNull() {
    	TransactionManager tm = null;
    	PermitManager pm = null;
        ParkingOffice office = new ParkingOffice(pm, tm);
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

        Car result = parkingOffice.register(car);
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

    @Test
    void testParkDelegatesToTransactionManager() {
        Customer customer = new Customer();
        UUID customerId = UUID.randomUUID();
        customer.setCustomerId(customerId);
        customer.setName("Alice");
        customer.setAddress(createTestAddress());
        customer.setPhoneNumber("555-1234");
        List<Car> cars = new ArrayList<>();
        customer.setCars(cars);
        parkingOffice.setCustomers(List.of(customer));

        Car car = new Car();
        car.setOwner(customerId);
        car.setLicense("ABC-123");
        car.setType(CarType.COMPACT);
        car.setPermit("Alice");
        car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(10));

        ParkingLot lot = new ParkingLot();
        lot.setLotId(UUID.randomUUID());
        lot.setCapacity(10);
        lot.setParkedCars(new HashSet<>());
        lot.setChargeOnExit(false);
        lot.setLotFee(new Money(200L));

        ParkingCharge charge = parkingOffice.park(LocalDateTime.now(), lot, car);
        assertNotNull(charge);
        assertTrue(lot.getParkedCars().contains(car));
    }

    @Test
    void testParkThrowsForUnknownCustomer() {
        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("P");

        ParkingLot lot = new ParkingLot();
        lot.setLotId(UUID.randomUUID());
        lot.setCapacity(10);
        lot.setParkedCars(new HashSet<>());
        lot.setChargeOnExit(false);
        lot.setLotFee(new Money(200L));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            parkingOffice.park(LocalDateTime.now(), lot, car)
        );
        assertTrue(ex.getMessage().contains("Unknown car owner"));
    }

    @Test
    void testLeaveThrowsAndRemovesCar() {
        ParkingLot lot = new ParkingLot();
        lot.setLotId(UUID.randomUUID());
        lot.setCapacity(10);
        Set<Car> parked = new HashSet<>();
        lot.setParkedCars(parked);
        lot.setChargeOnExit(true);
        lot.setLotFee(new Money(100L));

        Car car = new Car();
        car.setOwner(UUID.randomUUID());
        car.setPermit("P");
        car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(10));
        lot.getParkedCars().add(car);

        // Since no charge in transactionManager, leave will throw from transactionManager.leave
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            parkingOffice.leave(Instant.now(), lot, car)
        );
        assertTrue(ex.getMessage().contains("Failed to Process Parking Exit"));
        // But car is removed
        assertFalse(lot.getParkedCars().contains(car));
    }

    @Test
    void testUpdateDailyFeesDelegatesToTransactionManager() {
        ParkingLot lot = new ParkingLot();
        lot.setLotId(UUID.randomUUID());
        lot.setCapacity(10);
        lot.setParkedCars(new HashSet<>());
        lot.setChargeOnExit(false);
        lot.setLotFee(new Money(100L));

        parkingOffice.setLots(List.of(lot));

        // No exception expected
        parkingOffice.updateDailyFees();
    }

    @Test
    void testCalculateCustomerMonthlyBillDelegatesToTransactionManager() {
        Customer customer = new Customer();
        UUID customerId = UUID.randomUUID();
        customer.setCustomerId(customerId);
        customer.setName("Alice");
        customer.setAddress(createTestAddress());
        customer.setPhoneNumber("555-1234");
        Car car = new Car();
        car.setOwner(customerId);
        car.setType(CarType.SUV);
        customer.setCars(List.of(car));

        parkingOffice.setCustomers(List.of(customer));

        // No exception expected
        parkingOffice.calculateCustomerMonthlyBill();
    }

    @Test
    void testEqualsAndHashCode() {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());
        ParkingOffice office2 = new ParkingOffice("Name", createTestAddress());

        assertEquals(office1, office2);
        assertEquals(office1.hashCode(), office2.hashCode());
    }

    @Test
    void testNotEqualsDifferentName() {
        ParkingOffice office1 = new ParkingOffice("Name1", createTestAddress());
        ParkingOffice office2 = new ParkingOffice("Name2", createTestAddress());

        assertNotEquals(office1, office2);
    }

    @Test
    void testNotEqualsDifferentAddress() {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());
        Address addr2 = createTestAddress();
        addr2.setCity("Different");
        ParkingOffice office2 = new ParkingOffice("Name", addr2);

        assertNotEquals(office1, office2);
    }

    @Test
    void testNotEqualsDifferentCustomers() {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());
        office1.setCustomers(List.of(new Customer()));

        ParkingOffice office2 = new ParkingOffice("Name", createTestAddress());
        office2.setCustomers(new ArrayList<>());

        assertNotEquals(office1, office2);
    }

    @Test
    void testNotEqualsDifferentLots() {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());
        office1.setLots(List.of(new ParkingLot()));

        ParkingOffice office2 = new ParkingOffice("Name", createTestAddress());
        office2.setLots(new ArrayList<>());

        assertNotEquals(office1, office2);
    }

    @Test
    void testNotEqualsNullName() {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());

        ParkingOffice office2 = new ParkingOffice(null, createTestAddress());

        assertNotEquals(office1, office2);
    }

    @Test
    void testNotEqualsNullAddress() {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());

        ParkingOffice office2 = new ParkingOffice("Name", null);

        assertNotEquals(office1, office2);
    }

    @Test
    void testNotEqualsNullCustomers() throws Exception {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());
        office1.setCustomers(List.of(new Customer()));

        ParkingOffice office2 = new ParkingOffice("Name", createTestAddress());
        // set customers to null
        java.lang.reflect.Field f = office2.getClass().getDeclaredField("customers");
        f.setAccessible(true);
        f.set(office2, null);

        assertNotEquals(office1, office2);
    }

    @Test
    void testNotEqualsNullCustomersReversed() throws Exception {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());
        // set customers to null
        java.lang.reflect.Field f = office1.getClass().getDeclaredField("customers");
        f.setAccessible(true);
        f.set(office1, null);

        ParkingOffice office2 = new ParkingOffice("Name", createTestAddress());
        office2.setCustomers(List.of(new Customer()));

        assertNotEquals(office1, office2);
    }

    @Test
    void testNotEqualsNullLots() throws Exception {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());
        office1.setLots(List.of(new ParkingLot()));

        ParkingOffice office2 = new ParkingOffice("Name", createTestAddress());
        // set lots to null
        java.lang.reflect.Field f = office2.getClass().getDeclaredField("lots");
        f.setAccessible(true);
        f.set(office2, null);

        assertNotEquals(office1, office2);
    }

    @Test
    void testNotEqualsNullLotsReversed() throws Exception {
        ParkingOffice office1 = new ParkingOffice("Name", createTestAddress());
        // set lots to null
        java.lang.reflect.Field f = office1.getClass().getDeclaredField("lots");
        f.setAccessible(true);
        f.set(office1, null);

        ParkingOffice office2 = new ParkingOffice("Name", createTestAddress());
        office2.setLots(List.of(new ParkingLot()));

        assertNotEquals(office1, office2);
    }
    
    @Test
    public void testRegisterCarUnknownCustomerThrows() {
        ParkingOffice office = new ParkingOffice();
        Car car = new Car();
        car.setPermit("ABC123");
        car.setType(CarType.COMPACT);
        car.setOwner(UUID.randomUUID());
        assertThrows(IllegalArgumentException.class, () -> {
            office.register(car);
        });
    }
    
    @Test
    public void testLeaveSuccess() {
        TransactionManager tm = mock(TransactionManager.class);
        ParkingCharge charge = new ParkingCharge();
        when(tm.leave(any(), any(), any())).thenReturn(charge);

        ParkingOffice office = new ParkingOffice(new PermitManager(), tm);

        ParkingLot lot = new ParkingLot();
        Car car = new Car();
        car.setPermit("ABC123");
        car.setType(CarType.COMPACT);
        car.setOwner(UUID.randomUUID());
        ParkingCharge result = office.leave(Instant.now(), lot, car);

        assertEquals(charge, result);
    }
    
    @Test
    public void testGetPermitIdsMultipleCustomers() {
        ParkingOffice office = new ParkingOffice();
        
        Customer c1 = new Customer();
        c1.setName("Alice");
        c1.setCustomerId(UUID.randomUUID());
        c1.setCars(new ArrayList<>());
        
        Customer c2 = new Customer();
        c2.setName("Bob");
        c2.setCustomerId(UUID.randomUUID());
        c2.setCars(new ArrayList<>());
        
        Car car1 = new Car();
        car1.setLicense("Car1");
        car1.setType(CarType.COMPACT);
        car1.setOwner(c1.getCustomerId());
        
        Car car2 = new Car();
        car2.setLicense("Car2");
        car2.setType(CarType.SUV);
        car2.setOwner(c2.getCustomerId());

        c1.getCars().add(car1);
        c2.getCars().add(car2);

        office.setCustomers(List.of(c1, c2));

        List<UUID> ids = office.getPermitIds();

        assertEquals(2, ids.size());
    }



}
