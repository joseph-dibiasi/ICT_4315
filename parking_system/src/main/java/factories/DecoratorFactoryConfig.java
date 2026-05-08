package factories;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

import enums.CarType;

/*
 * Config class used by the ParkingChargeCalculatorFactory. Rate modifier is
 * used by all pricing decorators, but the rest are checked individually at
 * runtime for validity only if the decorator requires it.
 */
public class DecoratorFactoryConfig {

	private List<CarType> carTypes;
	private List<DayOfWeek> daysOfWeek;
	private List<Integer> specialDays;
	private Instant[] timeOfDayRange;
	/**
	 * Rate modifier interpreted as a multiplier. Example: 0.8 = 20% discount, 1.2 =
	 * 20% surcharge. All strategies should treat this value as a multiplicative
	 * factor applied to the current charge.
	 */
	private Double rateModifier;

	// construct from builder
	public DecoratorFactoryConfig(DecoratorFactoryConfigBuilder b) {
		this.carTypes = b.carTypes;
		this.daysOfWeek = b.daysOfWeek;
		this.specialDays = b.specialDays;
		this.timeOfDayRange = b.timeOfDayRange;
		this.rateModifier = b.rateModifier;
	}

	public List<CarType> getCarTypes() {
		return carTypes;
	}

	public List<DayOfWeek> getDaysOfWeek() {
		return daysOfWeek;
	}

	public List<Integer> getSpecialDays() {
		return specialDays;
	}

	public Instant[] getTimeOfDayRange() {
		return timeOfDayRange;
	}

	public Double getRateModifier() {
		return rateModifier;
	}

	public static class DecoratorFactoryConfigBuilder {
		private List<CarType> carTypes;
		private List<DayOfWeek> daysOfWeek;
		private List<Integer> specialDays;
		private Instant[] timeOfDayRange;
		private Double rateModifier;

		public DecoratorFactoryConfigBuilder() {
		}

		public DecoratorFactoryConfigBuilder carTypes(List<CarType> carTypes) {
			this.carTypes = carTypes;
			return this;
		}

		public DecoratorFactoryConfigBuilder daysOfWeek(List<DayOfWeek> daysOfWeek) {
			this.daysOfWeek = daysOfWeek;
			return this;
		}

		public DecoratorFactoryConfigBuilder specialDays(List<Integer> specialDays) {
			this.specialDays = specialDays;
			return this;
		}

		public DecoratorFactoryConfigBuilder timeOfDayRange(Instant[] timeOfDay) {
			this.timeOfDayRange = timeOfDay;
			return this;
		}

		public DecoratorFactoryConfigBuilder rateModifier(Double rateModifier) {
			this.rateModifier = rateModifier;
			return this;
		}

		public DecoratorFactoryConfig build() {
			return new DecoratorFactoryConfig(this);
		}
	}

	public static DecoratorFactoryConfigBuilder builder() {
		return new DecoratorFactoryConfigBuilder();
	}

}
