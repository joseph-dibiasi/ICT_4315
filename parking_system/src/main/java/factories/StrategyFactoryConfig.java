package factories;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

import enums.CarType;

/*
 * Config class used by the ParkingChargeStrategyFactory. Rate modifier is used
 * by all strategies, but the rest are checked individually at runtime for
 * validity only if the strategy requires it.
 */
public class StrategyFactoryConfig {

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
	public StrategyFactoryConfig(StrategyFactoryConfigBuilder b) {
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

	public static class StrategyFactoryConfigBuilder {
		private List<CarType> carTypes;
		private List<DayOfWeek> daysOfWeek;
		private List<Integer> specialDays;
		private Instant[] timeOfDayRange;
		private Double rateModifier;

		public StrategyFactoryConfigBuilder() {
		}

		public StrategyFactoryConfigBuilder carTypes(List<CarType> carTypes) {
			this.carTypes = carTypes;
			return this;
		}

		public StrategyFactoryConfigBuilder daysOfWeek(List<DayOfWeek> daysOfWeek) {
			this.daysOfWeek = daysOfWeek;
			return this;
		}

		public StrategyFactoryConfigBuilder specialDays(List<Integer> specialDays) {
			this.specialDays = specialDays;
			return this;
		}

		public StrategyFactoryConfigBuilder timeOfDayRange(Instant[] timeOfDay) {
			this.timeOfDayRange = timeOfDay;
			return this;
		}

		public StrategyFactoryConfigBuilder rateModifier(Double rateModifier) {
			this.rateModifier = rateModifier;
			return this;
		}

		public StrategyFactoryConfig build() {
			return new StrategyFactoryConfig(this);
		}
	}

	public static StrategyFactoryConfigBuilder builder() {
		return new StrategyFactoryConfigBuilder();
	}

}
