package models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ParkingOffice {

	private String name;
	private Address address;
	private List<Customer> customers;
	private List<ParkingLot> lots;

	// managers
	private PermitManager permitManager;
	private TransactionManager transactionManager;

	public ParkingOffice() {
		this(new PermitManager(), new TransactionManager());
		this.customers = new ArrayList<>();
		this.lots = new ArrayList<>();

	}

	public ParkingOffice(PermitManager permitManager, TransactionManager transactionManager) {
		this.customers = new ArrayList<>();
		this.lots = new ArrayList<>();
		this.permitManager = permitManager != null ? permitManager : new PermitManager();
		this.transactionManager = transactionManager != null ? transactionManager : new TransactionManager();
	}

	public ParkingOffice(String name, Address address) {
		this.name = name;
		this.address = address;
		this.customers = new ArrayList<>();
		this.lots = new ArrayList<>();
		this.permitManager = new PermitManager();
		this.transactionManager = new TransactionManager();
	}

	public void register(Customer customer) {
		permitManager.register(customer.getName(), customer.getAddress(), customer.getPhoneNumber());
	}

	public Car register(Car car) {
		Customer customer = getCustomer(car.getOwner());
		if (customer == null) {
			throw new IllegalArgumentException("Unknown car owner: " + car.getOwner());
		}
		Car registeredCar = permitManager.registerCar(customer.getCustomerId(), customer.getName(), car.getLicense(),
				car.getType());
		customer.getCars().add(registeredCar);
		return registeredCar;
	}

	public ParkingCharge park(LocalDateTime date, ParkingLot lot, Car car) {
		Customer customer = getCustomer(car.getOwner());
		if (customer == null) {
			throw new IllegalArgumentException("Unknown car owner: " + car.getOwner());
		}
		return transactionManager.park(date, lot, car);
	}

	public ParkingCharge leave(Instant exitTime, ParkingLot lot, Car car) {
		try {
			return transactionManager.leave(exitTime, lot, car);
		} catch (Exception e) {
			lot.getParkedCars().remove(car);
			throw new RuntimeException("Failed to Process Parking Exit: " + e.getMessage());
		}
	}

	public void updateDailyFees() {
		transactionManager.updateDailyFees(lots);
	}

	public void calculateCustomerMonthlyBill() {
		for (Customer customer : this.customers) {
			transactionManager.calculateCustomerMonthlyBill(customer);
		}
	}

	/**
	 * Return collection of all customer ids.
	 */
	public List<UUID> getCustomerIds() {
		return this.customers.stream().map(Customer::getCustomerId).toList();
	}

	/**
	 * Return distinct collection of all permit ids (derived from cars' owner ids).
	 */
	public List<UUID> getPermitIds() {
		List<Car> allCustomerCars = this.customers.stream().flatMap(c -> c.getCars().stream()).toList();

		return allCustomerCars.stream().map(Car::getOwner).distinct().toList();
	}

	/**
	 * Return distinct collection of permit ids for the specified customer.
	 */
	public List<UUID> getPermitIds(Customer customer) {
		if (customer == null) {
			return List.of();
		}
		return customer.getCars().stream().map(Car::getOwner).distinct().toList();
	}

	/*
	 * For a method to return only a single customer, name is not reliable. A large
	 * university could have several people with the same name; using the unique
	 * customerId is more reliable.
	 */
	public Customer getCustomer(UUID customerId) {
		return this.getCustomers().stream().filter(c -> c.getCustomerId().equals(customerId)).findFirst().orElse(null);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public List<ParkingLot> getLots() {
		return lots;
	}

	public void setLots(List<ParkingLot> lots) {
		this.lots = lots;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, customers, lots, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParkingOffice other = (ParkingOffice) obj;
		return Objects.equals(address, other.address) && Objects.equals(customers, other.customers)
				&& Objects.equals(lots, other.lots) && Objects.equals(name, other.name);
	}

}
