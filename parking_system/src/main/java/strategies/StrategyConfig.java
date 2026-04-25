package strategies;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

import enums.CarType;

public class StrategyConfig {

	private List<CarType> carTypes;
	private List<DayOfWeek> daysOfWeek;
	private List<Integer> specialDays;
	private Instant[] timeOfDayRange;
	private Double rateModifier;

	// construct from builder
	public StrategyConfig(StrategyConfigBuilder b) {
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

	public static class StrategyConfigBuilder {
		private List<CarType> carTypes;
		private List<DayOfWeek> daysOfWeek;
		private List<Integer> specialDays;
		private Instant[] timeOfDayRange;
		private Double rateModifier;

		public StrategyConfigBuilder() {
		}

		public StrategyConfigBuilder carType(List<CarType> carTypes) {
			this.carTypes = carTypes;
			return this;
		}

		public StrategyConfigBuilder daysOfWeek(List<DayOfWeek> daysOfWeek) {
			this.daysOfWeek = daysOfWeek;
			return this;
		}

		public StrategyConfigBuilder specialDays(List<Integer> specialDays) {
			this.specialDays = specialDays;
			return this;
		}

		public StrategyConfigBuilder timeOfDayRange(Instant[] timeOfDay) {
			this.timeOfDayRange = timeOfDay;
			return this;
		}

		public StrategyConfigBuilder rateModifier(Double rateModifier) {
			this.rateModifier = rateModifier;
			return this;
		}

		public StrategyConfig build() {
			return new StrategyConfig(this);
		}
	}

	public static StrategyConfigBuilder builder() {
		return new StrategyConfigBuilder();
	}

}
