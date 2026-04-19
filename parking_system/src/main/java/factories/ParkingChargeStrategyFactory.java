//package factories;
//
//import enums.StrategyType;
//import strategies.ParkingStrategy;
//import strategies.CarTypeStrategy;
//import strategies.DayOfWeekStrategy;
//import strategies.SpecialDaysStrategy;
//import strategies.TimeOfDayStrategy;
//
//public class ParkingChargeStrategyFactory {
	
//    public static ParkingStrategy createStrategy(StrategyType type) {
//
//        switch (type) {
//            case DAY_OF_WEEK:
//                return new DayOfWeekStrategy();
//
//            case SPECIAL_DAYS:
//                return new SpecialDaysStrategy();
//
//            case TIME_OF_DAY:
//                return new TimeOfDayStrategy();
// 
//            case CAR_TYPE:
//            	return new CarTypeStrategy();
//
//            default:
//                throw new IllegalArgumentException("Unknown strategy");
//        }
//    }
    
//}
