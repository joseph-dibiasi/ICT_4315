package models;

import java.util.Objects;

public class Address {

	private String streetAddress1;
	
	private String streetAddress2;
	
	private String city;
	
	private String state;
	
	private String zipCode;
	
	public String getAddressInfo() {
		return getStreetAddress1() + " " + getStreetAddress2() + ", " + getCity() + ", " + getState() + " " + getZipCode();
	}

	public String getStreetAddress1() {
		return streetAddress1;
	}

	public void setStreetAddress1(String streetAddress1) {
		this.streetAddress1 = streetAddress1;
	}

	public String getStreetAddress2() {
		return streetAddress2;
	}

	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(city, state, streetAddress1, streetAddress2, zipCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		return Objects.equals(city, other.city) && Objects.equals(state, other.state)
				&& Objects.equals(streetAddress1, other.streetAddress1)
				&& Objects.equals(streetAddress2, other.streetAddress2) && Objects.equals(zipCode, other.zipCode);
	}
}
