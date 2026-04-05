package models;

import java.time.LocalDate;
import java.util.UUID;

/**
 * This is the manager class which manages all the parking permits.
 */
public class PermitManager {

	public Customer register(String name, Address address, String phone) {
		Customer customer = new Customer();
		customer.setCustomerId(UUID.randomUUID());
		customer.setName(name);
		customer.setAddress(address);
		customer.setPhoneNumber(phone);
		return customer;
	}

	public Customer register(Customer customer, String license, CarType type) {
		Car car = registerCar(customer.getCustomerId(), customer.getName(), license, type);
		customer.getCars().add(car);
		return customer;

	}
	
	/*
	 * Every car registered to a customer is valid for one year from the date of
	 * registration. A customer can register multiple cars.
	 */
	public Car registerCar(UUID customerId, String name, String license, CarType carType) {
		Car registeredCar = new Car(customerId, name, LocalDate.now().plusYears(1), license, carType);

		return registeredCar;
	}

}
