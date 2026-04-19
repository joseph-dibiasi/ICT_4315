package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import strategies.ParkingStrategy;

public class ParkingLot {

	// Parking Lot ID. Unique.
	private UUID lotId;

	private Address address;

	private Integer capacity;

	// True = Hourly Rate. False = Daily Rate.
	private Boolean chargeOnExit;

	private Money lotFee;

	private Set<Car> parkedCars;
		
	// Strategies are implemented as a list to allow maximum flexibility in how many are applied per lot. 
	private List<ParkingStrategy> strategies;

	public UUID getLotId() {
		return lotId;
	}

	public void setLotId(UUID lotId) {
		this.lotId = lotId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return "ParkingLot [lotId=" + lotId + ", address=" + address + ", capacity=" + capacity + "]";
	}

	public Boolean getChargeOnExit() {
		return chargeOnExit;
	}

	public void setChargeOnExit(Boolean chargeOnExit) {
		this.chargeOnExit = chargeOnExit;
	}

	public Money getLotFee() {
		return lotFee;
	}

	public void setLotFee(Money lotFee) {
		this.lotFee = lotFee;
	}

	public Set<Car> getParkedCars() {
		if (parkedCars == null) {
			setParkedCars(new HashSet<>());
		}
		return parkedCars;
	}

	public void setParkedCars(Set<Car> parkedCars) {
		this.parkedCars = parkedCars;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, capacity, chargeOnExit, lotFee, lotId, parkedCars, strategies);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParkingLot other = (ParkingLot) obj;
		return Objects.equals(address, other.address) && Objects.equals(capacity, other.capacity)
				&& Objects.equals(chargeOnExit, other.chargeOnExit) && Objects.equals(lotFee, other.lotFee)
				&& Objects.equals(lotId, other.lotId) && Objects.equals(parkedCars, other.parkedCars)
				&& Objects.equals(strategies, other.strategies);
	}

	public List<ParkingStrategy> getStrategies() {
		if (strategies == null) {
			strategies = new ArrayList<>();
		}
		return strategies;
	}

	public void setStrategies(List<ParkingStrategy> strategies) {
		this.strategies = strategies;
	}

}
