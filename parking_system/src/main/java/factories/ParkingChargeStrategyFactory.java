package factories;
import enums.StrategyType;
import strategies.ParkingStrategy;
import strategies.CarTypeStrategy;
import strategies.DayOfWeekStrategy;
import strategies.SpecialDaysStrategy;
import strategies.StrategyConfig;
import strategies.TimeOfDayStrategy;

public class ParkingChargeStrategyFactory {

    public static ParkingStrategy createStrategy(StrategyType type, StrategyConfig config) {

        switch (type) {
            case DAY_OF_WEEK:
                return new DayOfWeekStrategy(config);

            case SPECIAL_DAYS:
                return new SpecialDaysStrategy(config);

            case TIME_OF_DAY:
                return new TimeOfDayStrategy(config);
 
            case CAR_TYPE:
            	return new CarTypeStrategy(config);

            default:
                throw new IllegalArgumentException("Unknown strategy");
        }
    }

}
