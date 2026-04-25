package factories;

import enums.StrategyType;
import strategies.ParkingStrategy;
import strategies.CarTypeStrategy;
import strategies.DayOfWeekStrategy;
import strategies.SpecialDaysStrategy;
import strategies.TimeOfDayStrategy;

public class ParkingChargeStrategyFactory {

	/*
	 * ParkingChargeStrategyFactory is responsible for creating instances of
	 * ParkingStrategy based on the provided StrategyType and configuration
	 * parameters. The factory validates parameters before instantiating the appropriate
	 * strategy class. Only the parameters relevant to the StrategyType are required.
	 */
	public static ParkingStrategy createStrategy(StrategyType type, StrategyFactoryConfig config) {

		if (config == null) {
			throw new IllegalArgumentException("Strategy Config cannot be null");
		}
		if (type == null) {
			throw new IllegalArgumentException("Strategy Type cannot be null");
		}
		if (config.getRateModifier() == null) {
			throw new IllegalArgumentException("Invalid configuration: Rate Modifier is required");
		}
		switch (type) {
		case DAY_OF_WEEK:
			if (config.getDaysOfWeek() == null) {
				throw new IllegalArgumentException("Invalid configuration: Days of Week required for this strategy");
			}
			return new DayOfWeekStrategy(config);

		case SPECIAL_DAYS:
			if (config.getSpecialDays() == null) {
				throw new IllegalArgumentException("Invalid configuration: Special Days required for this strategy");
			}
			return new SpecialDaysStrategy(config);

		case TIME_OF_DAY:
			if (config.getTimeOfDayRange() == null) {
				throw new IllegalArgumentException("Invalid configuration: Time of Day required for this strategy");
			}
			return new TimeOfDayStrategy(config);

		case CAR_TYPE:
			if (config.getCarTypes() == null) {
				throw new IllegalArgumentException("Invalid configuration: Car Types required for this strategy");
			}
			return new CarTypeStrategy(config);

		default:
			throw new IllegalArgumentException("Unknown strategy");
		}
	}

}
