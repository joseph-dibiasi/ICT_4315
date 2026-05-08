package factories;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import decorators.BaseParkingChargeCalculator;
import decorators.CarTypeDecorator;
import decorators.DayOfWeekDecorator;
import decorators.ParkingChargeCalculator;
import decorators.SpecialDaysDecorator;
import decorators.TimeOfDayDecorator;
import dtos.ParkingStrategyDTO;
import enums.DecoratorType;

class ParkingChargeCalculatorFactoryTest {

	@Test
	void createCalculatorWithNullRulesReturnsBaseCalculator() {
		ParkingChargeCalculator calculator = ParkingChargeCalculatorFactory.createCalculator(null);
		assertTrue(calculator instanceof BaseParkingChargeCalculator);
	}

	@Test
	void createCalculatorWithMultipleRulesBuildsDecoratorChain() {
		DecoratorFactoryConfig dayCfg = DecoratorFactoryConfig.builder()
				.daysOfWeek(List.of(java.time.DayOfWeek.SATURDAY)).rateModifier(0.9).build();
		DecoratorFactoryConfig carCfg = DecoratorFactoryConfig.builder()
				.carTypes(List.of(enums.CarType.COMPACT)).rateModifier(0.8).build();

		ParkingStrategyDTO dayDto = new ParkingStrategyDTO();
		dayDto.setDecoratorType(DecoratorType.DAY_OF_WEEK);
		dayDto.setDecoratorConfig(dayCfg);

		ParkingStrategyDTO carDto = new ParkingStrategyDTO();
		carDto.setDecoratorType(DecoratorType.CAR_TYPE);
		carDto.setDecoratorConfig(carCfg);

		ParkingChargeCalculator calculator = ParkingChargeCalculatorFactory.createCalculator(List.of(dayDto, carDto));

		assertTrue(calculator instanceof CarTypeDecorator);
	}

	@Test
	void createDayOfWeekDecorator() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().daysOfWeek(List.of(java.time.DayOfWeek.SATURDAY))
				.rateModifier(0.9).build();
		ParkingStrategyDTO dto = new ParkingStrategyDTO();
		dto.setDecoratorType(DecoratorType.DAY_OF_WEEK);
		dto.setDecoratorConfig(cfg);

		ParkingChargeCalculator calculator = ParkingChargeCalculatorFactory.createCalculator(List.of(dto));

		assertTrue(calculator instanceof DayOfWeekDecorator);
	}

	@Test
	void createSpecialDaysDecorator() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().specialDays(List.of(25)).rateModifier(0.8).build();
		ParkingStrategyDTO dto = new ParkingStrategyDTO();
		dto.setDecoratorType(DecoratorType.SPECIAL_DAYS);
		dto.setDecoratorConfig(cfg);

		ParkingChargeCalculator calculator = ParkingChargeCalculatorFactory.createCalculator(List.of(dto));

		assertTrue(calculator instanceof SpecialDaysDecorator);
	}

	@Test
	void createTimeOfDayDecorator() {
		Instant start = Instant.now();
		Instant end = start.plusSeconds(3600);
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().timeOfDayRange(new Instant[] { start, end })
				.rateModifier(1.2).build();
		ParkingStrategyDTO dto = new ParkingStrategyDTO();
		dto.setDecoratorType(DecoratorType.TIME_OF_DAY);
		dto.setDecoratorConfig(cfg);

		ParkingChargeCalculator calculator = ParkingChargeCalculatorFactory.createCalculator(List.of(dto));

		assertTrue(calculator instanceof TimeOfDayDecorator);
	}

	@Test
	void createCarTypeDecorator() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().carTypes(List.of(enums.CarType.COMPACT))
				.rateModifier(0.8).build();
		ParkingStrategyDTO dto = new ParkingStrategyDTO();
		dto.setDecoratorType(DecoratorType.CAR_TYPE);
		dto.setDecoratorConfig(cfg);

		ParkingChargeCalculator calculator = ParkingChargeCalculatorFactory.createCalculator(List.of(dto));

		assertTrue(calculator instanceof CarTypeDecorator);
	}

	@Test
	void nullConfigThrows() {
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeCalculatorFactory.decorate(new BaseParkingChargeCalculator(), DecoratorType.CAR_TYPE, null));
	}

	@Test
	void nullTypeThrows() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().rateModifier(1.0).build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeCalculatorFactory.decorate(new BaseParkingChargeCalculator(), null, cfg));
	}

	@Test
	void missingRateModifierThrows() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().daysOfWeek(List.of(java.time.DayOfWeek.SATURDAY))
				.build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeCalculatorFactory.decorate(new BaseParkingChargeCalculator(), DecoratorType.DAY_OF_WEEK, cfg));
	}

	@Test
	void missingDayOfWeekConfigThrows() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().rateModifier(0.8).build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeCalculatorFactory.decorate(new BaseParkingChargeCalculator(), DecoratorType.DAY_OF_WEEK, cfg));
	}

	@Test
	void missingSpecialDaysConfigThrows() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().rateModifier(0.8).build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeCalculatorFactory.decorate(new BaseParkingChargeCalculator(), DecoratorType.SPECIAL_DAYS, cfg));
	}

	@Test
	void missingTimeOfDayConfigThrows() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().rateModifier(0.8).build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeCalculatorFactory.decorate(new BaseParkingChargeCalculator(), DecoratorType.TIME_OF_DAY, cfg));
	}

	@Test
	void missingCarTypesConfigThrows() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().rateModifier(0.8).build();
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeCalculatorFactory.decorate(new BaseParkingChargeCalculator(), DecoratorType.CAR_TYPE, cfg));
	}

	/*
	 * I tried to cover the default case in the switch statement with a test that
	 * mocks an unknown StrategyType, but Mockito doesn't work with enums like I
	 * expected. Leaving it in as a lesson learned and a possible future enhancement
	 * to the factory to handle unknown types more gracefully.
	 */
	@Test
	void unknownConfigThrows() {
		DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().rateModifier(0.8).build();
		DecoratorType unknownType = Mockito.mock(DecoratorType.class);
		assertThrows(IllegalArgumentException.class,
				() -> ParkingChargeCalculatorFactory.decorate(new BaseParkingChargeCalculator(), unknownType, cfg));
	}

	@Test
	void constructorIsAccessible() {
		ParkingChargeCalculatorFactory factory = new ParkingChargeCalculatorFactory();
		assertNotNull(factory);
	}

}
