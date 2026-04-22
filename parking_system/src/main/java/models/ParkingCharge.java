package models;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ParkingCharge {

	// Customer ID. Unique.
	private UUID permitId;
	// Parking Lot ID. Unique.
	private UUID lotId;
	private Instant incurred;
	private Money amount;

	public UUID getPermitId() {
		return permitId;
	}

	public void setPermitId(UUID permitId) {
		this.permitId = permitId;
	}

	public UUID getLotId() {
		return lotId;
	}

	public void setLotId(UUID lotId) {
		this.lotId = lotId;
	}

	public Instant getIncurred() {
		return incurred;
	}

	public void setIncurred(Instant incurred) {
		this.incurred = incurred;
	}

	public Money getAmount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "ParkingCharge [permidId=" + permitId + ", lotId=" + lotId + ", incurred=" + incurred + ", amount="
				+ amount + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(amount, incurred, lotId, permitId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParkingCharge other = (ParkingCharge) obj;
		return Objects.equals(amount, other.amount) && Objects.equals(incurred, other.incurred)
				&& Objects.equals(lotId, other.lotId) && Objects.equals(permitId, other.permitId);
	}

	// construct from builder
	public ParkingCharge(ParkingChargeBuilder b) {
		this.permitId = b.permitId;
		this.lotId = b.lotId;
		this.incurred = b.incurred;
		this.amount = b.amount;
	}

	public static class ParkingChargeBuilder {
		private UUID permitId;
		private UUID lotId;
		private Instant incurred;
		private Money amount;

		public ParkingChargeBuilder() {
		}

		public ParkingChargeBuilder(UUID permitId, UUID lotId) {
			this.permitId = permitId;
			this.lotId = lotId;
		}

		public ParkingChargeBuilder permitId(UUID permitId) {
			this.permitId = permitId;
			return this;
		}

		public ParkingChargeBuilder lotId(UUID lotId) {
			this.lotId = lotId;
			return this;
		}

		public ParkingChargeBuilder incurred(Instant incurred) {
			this.incurred = incurred;
			return this;
		}

		public ParkingChargeBuilder amount(Money amount) {
			this.amount = amount;
			return this;
		}

		public ParkingCharge build() {
			return new ParkingCharge(this);
		}
	}

	/*
	 * Permit and Lot ID seemed like crucial fields for identifying ParkingCharge
	 * instances and so a builder was included with those initial fields.
	 */
	public static ParkingChargeBuilder builder() {
		return new ParkingChargeBuilder();
	}

	public static ParkingChargeBuilder builder(UUID permitId, UUID lotId) {
		return new ParkingChargeBuilder(permitId, lotId);
	}

}
