package factories;

import java.util.List;

import decorators.BaseParkingChargeCalculator;
import decorators.CarTypeDecorator;
import decorators.DayOfWeekDecorator;
import decorators.ParkingChargeCalculator;
import decorators.SpecialDaysDecorator;
import decorators.TimeOfDayDecorator;
import dtos.ParkingStrategyDTO;
import enums.DecoratorType;

/*
 * ParkingChargeCalculatorFactory is responsible for creating instances of ParkingChargeCaculators
 * and decorating them with variations of ParkingChargeDecorator based on the provided decorator type and configuration
 * parameters. The factory validates parameters before instantiating the appropriate
 * decorator class. Only the parameters relevant to each Decorator are required.
 */
public class ParkingChargeCalculatorFactory {

	public static ParkingChargeCalculator createCalculator(List<ParkingStrategyDTO> pricingRules) {
		ParkingChargeCalculator calculator = new BaseParkingChargeCalculator();

		if (pricingRules == null) {
			return calculator;
		}

		for (ParkingStrategyDTO pricingRule : pricingRules) {
			calculator = decorate(calculator, pricingRule.getDecoratorType(), pricingRule.getDecoratorConfig());
		}

		return calculator;
	}

	public static ParkingChargeCalculator decorate(ParkingChargeCalculator calculator, DecoratorType type,
			DecoratorFactoryConfig config) {
		validateConfig(type, config);

		switch (type) {
		case DAY_OF_WEEK:
			return new DayOfWeekDecorator(calculator, config);
		case SPECIAL_DAYS:
			return new SpecialDaysDecorator(calculator, config);
		case TIME_OF_DAY:
			return new TimeOfDayDecorator(calculator, config);
		case CAR_TYPE:
			return new CarTypeDecorator(calculator, config);
		default:
			throw new IllegalArgumentException("Unknown pricing decorator");
		}
	}

	private static void validateConfig(DecoratorType type, DecoratorFactoryConfig config) {
		if (config == null) {
			throw new IllegalArgumentException("Decorator Config cannot be null");
		}
		if (type == null) {
			throw new IllegalArgumentException("Decorator Type cannot be null");
		}
		if (config.getRateModifier() == null) {
			throw new IllegalArgumentException("Invalid configuration: Rate Modifier is required");
		}
		switch (type) {
		case DAY_OF_WEEK:
			if (config.getDaysOfWeek() == null) {
				throw new IllegalArgumentException("Invalid configuration: Days of Week required for this strategy");
			}
			break;
		case SPECIAL_DAYS:
			if (config.getSpecialDays() == null) {
				throw new IllegalArgumentException("Invalid configuration: Special Days required for this strategy");
			}
			break;
		case TIME_OF_DAY:
			if (config.getTimeOfDayRange() == null) {
				throw new IllegalArgumentException("Invalid configuration: Time of Day required for this strategy");
			}
			break;
		case CAR_TYPE:
			if (config.getCarTypes() == null) {
				throw new IllegalArgumentException("Invalid configuration: Car Types required for this strategy");
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown pricing decorator");
		}
	}

}
