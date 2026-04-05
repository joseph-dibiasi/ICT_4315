package models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Car {

	public Car() {
	}

	public Car(UUID owner, String permit, LocalDate permitExpiration, String license, CarType type) {
		super();
		this.permit = permit;
		this.permitExpiration = permitExpiration;
		this.license = license;
		this.type = type;
		this.owner = owner;
	}

	// Customer name. Not unique.
	private String permit;

	private LocalDate permitExpiration;

	// License plate number. Unique.
	private String license;

	private CarType type;

	// Customer ID. Unique.
	private UUID owner;

	public String getPermit() {
		return permit;
	}

	public void setPermit(String permit) {
		this.permit = permit;
	}

	public LocalDate getPermitExpiration() {
		return permitExpiration;
	}

	public void setPermitExpiration(LocalDate permitExpiration) {
		this.permitExpiration = permitExpiration;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public CarType getType() {
		return type;
	}

	public void setType(CarType type) {
		this.type = type;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Car [permit=" + permit + ", permitExpiration=" + permitExpiration + ", license=" + license + ", type="
				+ type + ", owner=" + owner + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(license, owner, permit, permitExpiration, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Car other = (Car) obj;
		return Objects.equals(license, other.license) && Objects.equals(owner, other.owner)
				&& Objects.equals(permit, other.permit) && Objects.equals(permitExpiration, other.permitExpiration)
				&& type == other.type;
	}


}
