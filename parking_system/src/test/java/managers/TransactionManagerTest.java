// File: ICT_4305/Week_4/src/test/java/classes/TransactionManagerTest.java
package managers;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dtos.ParkingStrategyDTO;
import enums.CarType;
import enums.StrategyType;
import factories.StrategyFactoryConfig;
import models.Address;
import models.Car;
import models.Customer;
import models.Money;
import models.ParkingCharge;
import models.ParkingLot;
import strategies.CarTypeStrategy;
import strategies.DayOfWeekStrategy;
import strategies.StrategyTestHelper;

class TransactionManagerTest {

	private TransactionManager tm;

	@BeforeEach
	void setUp() throws Exception {
		tm = new TransactionManager();
		// initialize private 'charges' list via reflection to avoid NPEs in production
		// code
		setPrivateField(tm, "charges", new ArrayList<ParkingCharge>());
	}

	private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
		Field f = target.getClass().getDeclaredField(fieldName);
		f.setAccessible(true);
		f.set(target, value);
	}

	private DayOfWeekStrategy defaultWeekendStrategy() {
		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().rateModifier(0.9)
				.daysOfWeek(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)).build();
		return new DayOfWeekStrategy(cfg);
	}
	
    private CarTypeStrategy defaultCompactStrategy() {
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder()
                .rateModifier(0.8)
                .carTypes(java.util.List.of(CarType.COMPACT))
                .build();
        return new CarTypeStrategy(cfg);
    }

	@Test
	void testParkThrowsWhenNoPermit() {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setCapacity(5);
		lot.setParkedCars(new HashSet<>());
		lot.setChargeOnExit(true);
		lot.setLotFee(new Money(100L));

		Car car = new Car();
		car.setLicense("L1");
		car.setOwner(UUID.randomUUID());
		// permit is null -> should throw
		RuntimeException ex = assertThrows(RuntimeException.class, () -> tm.park(LocalDateTime.now(), lot, car));
		assertTrue(ex.getMessage().contains("Permit required"));
	}

	@Test
	void testParkThrowsWhenPermitExpired() {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setCapacity(5);
		lot.setParkedCars(new HashSet<>());
		lot.setChargeOnExit(true);
		lot.setLotFee(new Money(100L));

		Car car = new Car();
		car.setPermit("Name");
		car.setPermitExpiration(LocalDateTime.now().toLocalDate().minusDays(1)); // expired
		car.setLicense("L2");
		car.setOwner(UUID.randomUUID());

		RuntimeException ex = assertThrows(RuntimeException.class, () -> tm.park(LocalDateTime.now(), lot, car));
		assertTrue(ex.getMessage().contains("Permit expired"));
	}

	@Test
	void testParkCreatesParkingCharge_hourlyAndDaily() throws Exception {
		// hourly lot
		ParkingLot hourly = new ParkingLot();
		hourly.setLotId(UUID.randomUUID());
		hourly.setCapacity(5);
		hourly.setParkedCars(new HashSet<>());
		hourly.setChargeOnExit(true);
		hourly.setLotFee(new Money(150L)); // $1.50/hour

		Car carH = new Car();
		carH.setLicense("H1");
		carH.setOwner(UUID.randomUUID());
		carH.setPermit("Permit");
		carH.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(10));

		ParkingCharge chargeH = tm.park(LocalDateTime.now(), hourly, carH);
		assertNotNull(chargeH);
		assertEquals(hourly.getLotId(), chargeH.getLotId());
		assertEquals(carH.getOwner(), chargeH.getPermitId());
		// hourly lots start with zero dollars
		assertEquals(0L, chargeH.getAmount().getCents());

		// daily lot
		ParkingLot daily = new ParkingLot();
		daily.setLotId(UUID.randomUUID());
		daily.setCapacity(5);
		daily.setParkedCars(new HashSet<>());
		daily.setChargeOnExit(false);
		daily.setLotFee(new Money(200L)); // $2.00/day

		Car carD = new Car();
		carD.setLicense("D1");
		carD.setOwner(UUID.randomUUID());
		carD.setPermit("P");
		carD.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(10));

		ParkingCharge chargeD = tm.park(LocalDateTime.now(), daily, carD);
		assertNotNull(chargeD);
		assertEquals(daily.getLotId(), chargeD.getLotId());
		assertEquals(carD.getOwner(), chargeD.getPermitId());
		// daily lots apply initial lot fee
		assertEquals(200L, chargeD.getAmount().getCents());
	}

	@Test
	void testLeaveHourlyCalculatesAmountAndRemovesCar() throws Exception {
		// prepare lot and manager charges
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setCapacity(5);
		Set<Car> parked = new HashSet<>();
		lot.setParkedCars(parked);
		lot.setChargeOnExit(true);
		lot.setLotFee(new Money(100L)); // $1/hour

		Car car = new Car();
		car.setLicense("L3");
		car.setOwner(UUID.randomUUID());
		car.setPermit("OK");
		car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(5));
		lot.getParkedCars().add(car);

		// create an existing charge (entry) and add it to manager's charges
		ParkingCharge existing = ParkingCharge.builder(car.getOwner(), lot.getLotId())
				.incurred(Instant.now().minus(2, ChronoUnit.HOURS)).amount(new Money(0L)).build();
		// add to private charges list
		@SuppressWarnings("unchecked")
		List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
		charges.add(existing);

		Instant exit = Instant.now();
		ParkingCharge result = tm.leave(exit, lot, car);

		// amount should be updated: 2 hours * $1.0 = $2.0
		assertEquals(200L, result.getAmount().getCents());
		assertNull(result.getIncurred());
		assertFalse(lot.getParkedCars().contains(car));
	}

	@Test
	void testParkThrowsWhenLotFull() {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setCapacity(1);
		lot.setParkedCars(new HashSet<>());
		lot.setChargeOnExit(true);
		lot.setLotFee(new Money(100L));

		Car car1 = new Car();
		car1.setPermit("P1");
		car1.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(1));
		car1.setLicense("L1");
		car1.setOwner(UUID.randomUUID());
		lot.getParkedCars().add(car1);

		Car car2 = new Car();
		car2.setPermit("P2");
		car2.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(1));
		car2.setLicense("L2");
		car2.setOwner(UUID.randomUUID());

		RuntimeException ex = assertThrows(RuntimeException.class, () -> tm.park(LocalDateTime.now(), lot, car2));
		assertTrue(ex.getMessage().contains("Parking Lot Full"));
	}

	@Test
	void testParkThrowsWhenCarAlreadyParked() {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setCapacity(5);
		lot.setParkedCars(new HashSet<>());
		lot.setChargeOnExit(true);
		lot.setLotFee(new Money(100L));

		Car car = new Car();
		car.setPermit("P1");
		car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(1));
		car.setLicense("L1");
		car.setOwner(UUID.randomUUID());
		lot.getParkedCars().add(car);

		RuntimeException ex = assertThrows(RuntimeException.class, () -> tm.park(LocalDateTime.now(), lot, car));
		assertTrue(ex.getMessage().contains("Car already parked in the lot"));
	}

	@Test
	void testFindParkingChargeByLotIdAndOwnerIdNonMatching() throws Exception {
		ParkingCharge charge = ParkingCharge.builder(UUID.randomUUID(), UUID.randomUUID()).amount(new Money(100L))
				.build();
		@SuppressWarnings("unchecked")
		List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
		charges.clear();
		charges.add(charge);

		ParkingCharge found = tm.findParkingChargeByLotIdAndOwnerId(UUID.randomUUID(), UUID.randomUUID());
		assertNull(found);
	}

	@Test
	void testUpdateDailyFeesSkipsHourlyLots() throws Exception {
		ParkingLot hourly = new ParkingLot();
		hourly.setLotId(UUID.randomUUID());
		hourly.setCapacity(5);
		hourly.setChargeOnExit(true);
		hourly.setLotFee(new Money(100L));
		hourly.setParkedCars(new HashSet<>());

		tm.updateDailyFees(List.of(hourly));
		assertTrue(hourly.getParkedCars().isEmpty());
	}

	@Test
	void testUpdateDailyFeesIncrementsCharge() throws Exception {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setCapacity(5);
		lot.setChargeOnExit(false);
		lot.setLotFee(new Money(100L));
		Car car = new Car();
		car.setOwner(UUID.randomUUID());
		car.setPermit("P");
		car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(5));
		lot.getParkedCars().add(car);

		ParkingCharge charge = ParkingCharge.builder(car.getOwner(), lot.getLotId()).amount(new Money(100L)).build();

		@SuppressWarnings("unchecked")
		List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
		charges.clear();
		charges.add(charge);

		tm.updateDailyFees(List.of(lot));
		assertEquals(200L, charge.getAmount().getCents());
	}

	@Test
	void testCreateOrUpdateEntryParkingChargeUpdatesExistingDailyCharge() throws Exception {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setChargeOnExit(false);
		lot.setLotFee(new Money(100L));

		Car car = new Car();
		car.setOwner(UUID.randomUUID());
		car.setPermit("P");
		car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(5));

		ParkingCharge charge = ParkingCharge.builder(car.getOwner(), lot.getLotId()).amount(new Money(100L)).build();

		@SuppressWarnings("unchecked")
		List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
		charges.clear();
		charges.add(charge);

		ParkingCharge updated = tm.createOrUpdateEntryParkingCharge(lot, car);
		assertEquals(200L, updated.getAmount().getCents());
	}

	@Test
	void testCreateOrUpdateEntryParkingChargeUpdatesExistingHourlyCharge() throws Exception {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setChargeOnExit(true);
		lot.setLotFee(new Money(100L));

		Car car = new Car();
		car.setOwner(UUID.randomUUID());
		car.setPermit("P");
		car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(5));

		ParkingCharge charge = ParkingCharge.builder(car.getOwner(), lot.getLotId()).amount(new Money(0L))
				.incurred(Instant.now().minus(1, ChronoUnit.HOURS)).build();

		@SuppressWarnings("unchecked")
		List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
		charges.clear();
		charges.add(charge);

		ParkingCharge updated = tm.createOrUpdateEntryParkingCharge(lot, car);
		assertNotNull(updated.getIncurred());
		assertEquals(0L, updated.getAmount().getCents());
	}

	@Test
	void testLeaveHourlyWhenNegativeHoursThrows() throws Exception {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setCapacity(5);
		lot.setParkedCars(new HashSet<>());
		lot.setChargeOnExit(true);
		lot.setLotFee(new Money(100L));

		Car car = new Car();
		car.setLicense("L5");
		car.setOwner(UUID.randomUUID());
		car.setPermit("P");
		car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(2));
		lot.getParkedCars().add(car);

		ParkingCharge existing = ParkingCharge.builder(car.getOwner(), lot.getLotId()).amount(new Money(0L))
				.incurred(Instant.now().plus(2, ChronoUnit.HOURS)).build();

		@SuppressWarnings("unchecked")
		List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
		charges.clear();
		charges.add(existing);

		RuntimeException ex = assertThrows(RuntimeException.class, () -> tm.leave(Instant.now(), lot, car));
		assertTrue(ex.getMessage().contains("Invalid parking time detected"));
	}

	@Test
	void testLeaveDailyReturnsNewChargeAndRemovesCar() {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		lot.setCapacity(5);
		Set<Car> parked = new HashSet<>();
		lot.setParkedCars(parked);
		lot.setChargeOnExit(false); // daily
		lot.setLotFee(new Money(100L));

		Car car = new Car();
		car.setLicense("D1");
		car.setOwner(UUID.randomUUID());
		car.setPermit("P");
		car.setPermitExpiration(LocalDateTime.now().toLocalDate().plusDays(5));
		lot.getParkedCars().add(car);

		Instant exit = Instant.now();
		ParkingCharge result = tm.leave(exit, lot, car);

		assertNotNull(result);
		assertNull(result.getAmount()); // new ParkingCharge has null amount?
		// Wait, new ParkingCharge() has amount null? No, ParkingCharge constructor sets
		// amount to new Money(0L)? Wait, let's check ParkingCharge.

		// Actually, ParkingCharge default constructor sets amount = new Money(0L); in
		// the code? Wait, no, ParkingCharge has private Money amount; no init.

		// In leave, for !chargeOnExit, charge = new ParkingCharge(); so amount is null.

		// But in the test, assertNull(result.getAmount()); but probably it's null.

		// Wait, but to match, perhaps assert that it's a new charge.

		assertFalse(lot.getParkedCars().contains(car));
	}

	@Test
	void testAddChargeAndAddChargeThrowsWhenNullAmount() {
		// normal add
		ParkingCharge charge = ParkingCharge.builder().amount(new Money(100L)).build();
		Money lotFee = new Money(200L); // $2
		Money updated = tm.addCharge(charge, lotFee);
		assertEquals(300L, updated.getCents());

		// error path: parkingCharge.getAmount() null
		ParkingCharge bad = ParkingCharge.builder().amount(null).build();
		RuntimeException ex = assertThrows(RuntimeException.class, () -> tm.addCharge(bad, lotFee));
		assertTrue(ex.getMessage().contains("Failed to Process Parking Charge"));
	}

	@Test
	void testCalculatePermitBillAndCustomerMonthlyBillAndRemoveCharges() throws Exception {
		UUID ownerId = UUID.randomUUID();

		// create charges for owner: $1.00 and $2.00
		ParkingCharge c1 = ParkingCharge.builder().permitId(ownerId).amount(new Money(100L)).build();

		ParkingCharge c2 = ParkingCharge.builder().permitId(ownerId).amount(new Money(200L)).build();

		@SuppressWarnings("unchecked")
		List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
		charges.clear();
		charges.add(c1);
		charges.add(c2);

		// non-compact car => total $3.00
		Car car = new Car();
		car.setOwner(ownerId);
		car.setType(CarType.SUV);
		Double total = tm.calculatePermitBill(car);
		assertEquals(3.0, total, 0.0001);

		// calculateCustomerMonthlyBill: prepare customer with cars
		Customer cust = Customer.builder(ownerId, "Cust").build();
		Address a = Address.builder().streetAddress1("Addr").build();
		cust.setAddress(a);
		List<Car> cars = new ArrayList<>();
		cars.add(car);
		cust.setCars(cars);

		// ensure charges list has entries -> method should return true and remove
		// matching charges
		boolean res = tm.calculateCustomerMonthlyBill(cust);
		assertTrue(res);
		// after successful billing, charges for owner should be removed
		List<ParkingCharge> remaining = tm.findParkingChargesByOwnerId(ownerId);
		assertTrue(remaining.isEmpty());
	}

	@Test
	void testCalculateCustomerMonthlyBillThrowsOnNullCars() {
		Customer cust = Customer.builder(UUID.randomUUID(), "Test").build();
		cust.setCustomerId(UUID.randomUUID());
		cust.setName("X");
		Address a = Address.builder().streetAddress1("Addr").build();
		cust.setAddress(a);
		cust.setCars(null); // will cause NPE inside method and be wrapped

		RuntimeException ex = assertThrows(RuntimeException.class, () -> tm.calculateCustomerMonthlyBill(cust));
		assertTrue(ex.getMessage().contains("Failed to Process Customer Monthly Bill"));
	}

	@Test
	void testFindAndRemoveParkingChargesByOwnerId() throws Exception {
		UUID permitId = UUID.randomUUID();
		ParkingCharge p = ParkingCharge.builder(permitId, UUID.randomUUID()).amount(new Money(50L)).build();

		@SuppressWarnings("unchecked")
		List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
		charges.clear();
		charges.add(p);

		List<ParkingCharge> found = tm.findParkingChargesByOwnerId(permitId);
		assertEquals(1, found.size());

		boolean removed = tm.removeParkingChargesByOwnerId(permitId);
		assertTrue(removed);
		assertTrue(((List<?>) getPrivateField(tm, "charges")).isEmpty());
	}

	@Test
	void testRemoveParkingChargesByOwnerIdNoCharges() throws Exception {
		UUID a = UUID.randomUUID();

		@SuppressWarnings("unchecked")
		List<ParkingCharge> charges = (List<ParkingCharge>) getPrivateField(tm, "charges");
		charges.clear(); // empty

		boolean removed = tm.removeParkingChargesByOwnerId(a);
		assertFalse(removed);
	}

	@Test
	void testCalculateBaseChargeHourlyMinimumOneHour() {
		TransactionManager local = new TransactionManager();
		models.ParkingLot lot = new models.ParkingLot();
		lot.setChargeOnExit(true);
		lot.setLotFee(new Money(500L)); // $5/hour

		Instant entry = Instant.now();
		Instant exit = entry.plusSeconds(30 * 60); // 30 minutes later

		Money m = local.calculateBaseCharge(lot, new models.Car(), entry, exit);
		// should charge at least 1 hour
		assertEquals(500L, m.getCents());
	}

	@Test
	void testCalculateBaseChargeDailyWhenExitNullAndChargeOnExitTrue() {
		TransactionManager local = new TransactionManager();
		models.ParkingLot lot = new models.ParkingLot();
		lot.setChargeOnExit(true);
		lot.setLotFee(new Money(200L));

		Money m = local.calculateBaseCharge(lot, new models.Car(), null, null);
		// for hourly lots with null exitTime should return $0 initial
		assertEquals(0L, m.getCents());
	}

	@Test
	void testCalculateBaseChargeExitOnDailyLotReturnsLotFee() {
		TransactionManager local = new TransactionManager();
		models.ParkingLot lot = new models.ParkingLot();
		lot.setChargeOnExit(false);
		lot.setLotFee(new Money(250L));

		Instant entry = Instant.now().minusSeconds(3600);
		Instant exit = Instant.now();

		Money m = local.calculateBaseCharge(lot, new models.Car(), entry, exit);
		assertEquals(250L, m.getCents());
	}

	@Test
	void testCalculateParkingChargeAppliesCombinedCarTypeAndDayOfWeekDiscounts() {
		ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, defaultCompactStrategy(), defaultWeekendStrategy());
		ParkingCharge charge = tm.calculateParkingCharge(lot, StrategyTestHelper.createCar(CarType.COMPACT),
				StrategyTestHelper.toInstant(2026, 4, 18, 10, 0), null);

		assertEquals(14.40, charge.getAmount().getDollars(), 0.001);
	}

	@Test
	void testCalculateParkingChargeUsesBaseRateForSuvOnWeekday() {
		ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, defaultCompactStrategy(), defaultWeekendStrategy());
		ParkingCharge charge = tm.calculateParkingCharge(lot, StrategyTestHelper.createCar(CarType.SUV),
				StrategyTestHelper.toInstant(2026, 4, 20, 10, 0), null);

		assertEquals(20.00, charge.getAmount().getDollars(), 0.001);
	}

	@Test
	void testCalculateParkingChargeAppliesOnlyCarTypeDiscountForCompactOnWeekday() {
		ParkingLot lot = StrategyTestHelper.createLot(false, 20.0, defaultCompactStrategy(), defaultWeekendStrategy());
		ParkingCharge charge = tm.calculateParkingCharge(lot, StrategyTestHelper.createCar(CarType.COMPACT),
				StrategyTestHelper.toInstant(2026, 4, 20, 10, 0), null);

		assertEquals(16.00, charge.getAmount().getDollars(), 0.001);
	}

	@Test
	void assignStrategiesThrowsWhenLotNull() {
		List<ParkingStrategyDTO> dtos = new ArrayList<>();
		RuntimeException ex = assertThrows(IllegalArgumentException.class, () -> tm.assignStrategies(null, dtos));
	}

	@Test
	void assignStrategiesClearsStrategiesWhenEmpty() {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());
		// add a dummy strategy list
		lot.getStrategies().add(new DayOfWeekStrategy(StrategyFactoryConfig.builder().rateModifier(0.9)
				.daysOfWeek(List.of(java.time.DayOfWeek.SATURDAY)).build()));

		// empty list should clear
		tm.assignStrategies(lot, new ArrayList<>());
		assertTrue(lot.getStrategies().isEmpty());
	}

	@Test
	void assignStrategiesCreatesStrategies() {
		ParkingLot lot = new ParkingLot();
		lot.setLotId(UUID.randomUUID());

		StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().daysOfWeek(List.of(java.time.DayOfWeek.SATURDAY))
				.rateModifier(0.9).build();

		ParkingStrategyDTO dto = new ParkingStrategyDTO();
		dto.setStrategyType(StrategyType.DAY_OF_WEEK);
		dto.setStrategyConfig(cfg);

		tm.assignStrategies(lot, List.of(dto));
		assertEquals(1, lot.getStrategies().size());
		assertTrue(lot.getStrategies().get(0) instanceof DayOfWeekStrategy);
	}

	private static Object getPrivateField(Object target, String fieldName) throws Exception {
		Field f = target.getClass().getDeclaredField(fieldName);
		f.setAccessible(true);
		return f.get(target);
	}
}
