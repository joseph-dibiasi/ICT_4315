package models;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Customer {

	// Campus ID. Unique.
	private UUID customerId;

	private String name;

	private Address address;

	private String phoneNumber;

	private List<Car> cars;

	public UUID getCustomerId() {
		return customerId;
	}

	public void setCustomerId(UUID customerId) {
		this.customerId = customerId;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<Car> getCars() {
		return cars;
	}

	public void setCars(List<Car> cars) {
		this.cars = cars;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", name=" + name + ", address=" + address.getAddressInfo()
				+ ", phoneNumber=" + phoneNumber + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, cars, customerId, name, phoneNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		return Objects.equals(address, other.address) && Objects.equals(cars, other.cars)
				&& Objects.equals(customerId, other.customerId) && Objects.equals(name, other.name)
				&& Objects.equals(phoneNumber, other.phoneNumber);
	}

	// construct from builder
	public Customer(CustomerBuilder b) {
		this.customerId = b.customerId;
		this.name = b.name;
		this.address = b.address;
		this.phoneNumber = b.phoneNumber;
		this.cars = b.cars;
	}

	public static class CustomerBuilder {
		private UUID customerId;
		private String name;
		private Address address;
		private String phoneNumber;
		private List<Car> cars;

		public CustomerBuilder() {
		}

		public CustomerBuilder(UUID customerId, String name) {
			this.customerId = customerId;
			this.name = name;
		}

		public CustomerBuilder customerId(UUID customerId) {
			this.customerId = customerId;
			return this;
		}

		public CustomerBuilder name(String name) {
			this.name = name;
			return this;
		}

		public CustomerBuilder address(Address address) {
			this.address = address;
			return this;
		}

		public CustomerBuilder phoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public CustomerBuilder cars(List<Car> cars) {
			this.cars = cars;
			return this;
		}

		public Customer build() {
			return new Customer(this);
		}
	}

	/*
	 * Customer ID seemed like a good choice for a required field and I hope all
	 * customers know their name.
	 */
	public static CustomerBuilder builder() {
		return new CustomerBuilder();
	}

	public static CustomerBuilder builder(UUID customerId, String name) {
		return new CustomerBuilder(customerId, name);
	}

}
