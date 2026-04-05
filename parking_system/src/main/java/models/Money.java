package models;

import java.util.Objects;

public class Money {
	
	public Money() {
		
	}
	
	public Money(Long cents) {
		this.cents = cents;
	}
	
	public Money(Double dollars) {
		this.cents = Math.round(dollars * 100);
	}

	private Long cents;

	public Long getCents() {
		return cents;
	}

	public void setCents(Long cents) {
		this.cents = cents;
	}

	public Double getDollars() {
		return this.cents / 100.0;
	}

	@Override
	public String toString() {
		return "Money [cents=" + cents + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(cents);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Money other = (Money) obj;
		return Objects.equals(cents, other.cents);
	}
}
