package models;

import java.util.Objects;

public class Address {

	private String streetAddress1;

	private String streetAddress2;

	private String city;

	private String state;

	private String zipCode;

	public String getAddressInfo() {
		return getStreetAddress1() + getStreetAddress2() + getCity() + ", " + getState() + " "
			+ getZipCode();
	}

	public String getStreetAddress1() {
		return streetAddress1;
	}

	public void setStreetAddress1(String streetAddress1) {
		this.streetAddress1 = streetAddress1;
	}

	public String getStreetAddress2() {
		if (streetAddress2 == null) {
			return ", ";
		}
		return " " + streetAddress2 + ", ";
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

	// construct from builder
	public Address(AddressBuilder b) {
		this.streetAddress1 = b.streetAddress1;
		this.streetAddress2 = b.streetAddress2;
		this.city = b.city;
		this.state = b.state;
		this.zipCode = b.zipCode;
	}

	public static class AddressBuilder {
		private String streetAddress1;
		private String streetAddress2;
		private String city;
		private String state;
		private String zipCode;

		public AddressBuilder() {
		}

		public AddressBuilder streetAddress1(String streetAddress1) {
			this.streetAddress1 = streetAddress1;
			return this;
		}

		public AddressBuilder streetAddress2(String streetAddress2) {
			this.streetAddress2 = streetAddress2;
			return this;
		}

		public AddressBuilder city(String city) {
			this.city = city;
			return this;
		}

		public AddressBuilder state(String state) {
			this.state = state;
			return this;
		}

		public AddressBuilder zipCode(String zipCode) {
			this.zipCode = zipCode;
			return this;
		}

		public Address build() {
			return new Address(this);
		}
	}

	/*
	 * Since Address seemed like every field would be required, I only used a
	 * default builder to construct it from scratch.
	 */
	public static AddressBuilder builder() {
		return new AddressBuilder();
	}
}
