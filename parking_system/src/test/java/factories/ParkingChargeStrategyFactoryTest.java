package factories;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import enums.StrategyType;
import strategies.CarTypeStrategy;
import strategies.DayOfWeekStrategy;
import strategies.SpecialDaysStrategy;
import strategies.TimeOfDayStrategy;

class ParkingChargeStrategyFactoryTest {

	@Test
	void createDayOfWeekStrategy() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().daysOfWeek(List.of(java.time.DayOfWeek.SATURDAY))
				.rateModifier(0.9).build();

		assertTrue(ParkingChargeStrategyFactory.createStrategy(StrategyType.DAY_OF_WEEK,
				cfg) instanceof DayOfWeekStrategy);
	}

	@Test
	void createSpecialDaysStrategy() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().specialDays(List.of(25)).rateModifier(0.8).build();

		assertTrue(ParkingChargeStrategyFactory.createStrategy(StrategyType.SPECIAL_DAYS,
				cfg) instanceof SpecialDaysStrategy);
	}

	@Test
	void createTimeOfDayStrategy() {
		Instant start = Instant.now();
		Instant end = start.plusSeconds(3600);
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().timeOfDayRange(new Instant[] { start, end })
				.rateModifier(1.2).build();

		assertTrue(ParkingChargeStrategyFactory.createStrategy(StrategyType.TIME_OF_DAY,
				cfg) instanceof TimeOfDayStrategy);
	}

	@Test
	void createCarTypeStrategy() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().carTypes(List.of(enums.CarType.COMPACT))
				.rateModifier(0.8).build();

		assertTrue(ParkingChargeStrategyFactory.createStrategy(StrategyType.CAR_TYPE, cfg) instanceof CarTypeStrategy);
	}

	@Test
	void nullConfigThrows() {
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeStrategyFactory.createStrategy(StrategyType.CAR_TYPE, null));
	}

	@Test
	void nullTypeThrows() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().rateModifier(1.0).build();
		assertThrows(IllegalArgumentException.class, () -> ParkingChargeStrategyFactory.createStrategy(null, cfg));
	}

	@Test
	void missingRateModifierThrows() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().daysOfWeek(List.of(java.time.DayOfWeek.SATURDAY))
				.build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeStrategyFactory.createStrategy(StrategyType.DAY_OF_WEEK, cfg));
	}

	@Test
	void missingDayOfWeekConfigThrows() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().rateModifier(0.8).build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeStrategyFactory.createStrategy(StrategyType.DAY_OF_WEEK, cfg));
	}

	@Test
	void missingSpecialDaysConfigThrows() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().rateModifier(0.8).build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeStrategyFactory.createStrategy(StrategyType.SPECIAL_DAYS, cfg));
	}

	@Test
	void missingTimeOfDayConfigThrows() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().rateModifier(0.8).build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeStrategyFactory.createStrategy(StrategyType.TIME_OF_DAY, cfg));
	}

	@Test
	void missingCarTypesConfigThrows() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().rateModifier(0.8).build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeStrategyFactory.createStrategy(StrategyType.CAR_TYPE, cfg));
	}

	/*
	 * I tried to cover the default case in the switch statement with a test that
	 * mocks an unknown StrategyType, but Mockito doesn't work with enums like I
	 * expected. Leaving it in as a lesson learned and a possible future enhancement
	 * to the factory to handle unknown types more gracefully.
	 */
	@Test
	void unknownConfigThrows() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().rateModifier(0.8).build();
		StrategyType unknownType = Mockito.mock(StrategyType.class);
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeStrategyFactory.createStrategy(unknownType, cfg));
	}

}
