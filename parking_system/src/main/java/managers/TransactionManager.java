package managers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dtos.ParkingStrategyDTO;
import factories.ParkingChargeStrategyFactory;
import models.Car;
import models.Customer;
import models.Money;
import models.ParkingCharge;
import models.ParkingCharge.ParkingChargeBuilder;
import models.ParkingLot;
import strategies.ParkingStrategy;

/**
 * This is the class that manages all the parking transactions.
 */
public class TransactionManager {
	private List<ParkingCharge> charges;

	public TransactionManager() {
		this.charges = new ArrayList<>();
	}

	/**
	 * Replace the strategies on the given ParkingLot with the provided list of
	 * strategies created via the factory. StrategyDTO contains StrategyType which
	 * dictates which factory is used to build the strategy. StrategyConfig contains
	 * the information needed to configure the built strategy. At the moment,
	 * assigning lot strategies is done wholesale; assigning new strategies replaces
	 * all existing strategies. Passing in no strategies functions as a removal
	 * feature. As the project continues, this is an area targeted for enhancement.
	 * It may be desirable to add/remove/modify specific strategies without
	 * affecting the rest.
	 */
	public void assignStrategies(ParkingLot lot, List<ParkingStrategyDTO> strategyDTOs) {
		if (lot == null) {
			throw new IllegalArgumentException("ParkingLot cannot be null");
		}

		if (lot.getStrategies() != null) {
			// Clear existing strategies before assigning new ones.
			lot.getStrategies().clear();
		}

		List<ParkingStrategy> newStrategies = new ArrayList<>();

		for (ParkingStrategyDTO parkingStrategyDTO : strategyDTOs) {
			newStrategies.add(ParkingChargeStrategyFactory.createStrategy(parkingStrategyDTO.getStrategyType(),
					parkingStrategyDTO.getStrategyConfig()));
		}

		lot.setStrategies(newStrategies);
	}

	/*
	 * This method is responsible for creating the entry ParkingFee object for each
	 * car, but there are several validations that need to be done first. If any
	 * validation fails, an exception is thrown and not caught. That is because any
	 * validation failure means a car is not allowed to enter the lot. While most of
	 * these errors would simply return a more graceful error message, the last
	 * checked exception could represent something more serious. The Parking Office
	 * may store this type of error for further inquiry.
	 */
	public ParkingCharge park(LocalDateTime date, ParkingLot lot, Car car) {
		try {
			if (car.getPermit() == null) {
				throw new RuntimeException("Permit required to enter parking lot.");
			}
			if (car.getPermitExpiration().isBefore(date.toLocalDate())) {
				throw new RuntimeException("Permit expired. Please contact Parking Office.");
			}
			if (lot.getParkedCars().size() >= lot.getCapacity()) {
				throw new RuntimeException("Parking Lot Full.");
			}
			if (lot.getParkedCars().contains(car)) {
				throw new RuntimeException("Car already parked in the lot.");
			}

			lot.getParkedCars().add(car);
			ParkingCharge charge = this.createOrUpdateEntryParkingCharge(lot, car);
			charges.add(charge);
			return charge;
		} catch (Exception e) {
			System.err.println("Failed to validate entry: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	/*
	 * When cars exit a lot, the car is removed from the lot's ParkedCars list. This
	 * prevents customers from being charged additional daily rates on their permit
	 * and allows new cars to park in the lot. If a lot charges an hourly rate, the
	 * total charge is calculated and added to the existing parking charges for the
	 * lot. If an error is thrown during this process, the system throws an error to
	 * alert the Parking Office but the car is still removed from the ParkedCars
	 * list to allow new cars to park.
	 */
	public ParkingCharge leave(Instant exitTime, ParkingLot lot, Car car) {
		ParkingCharge charge;
		if (lot.getChargeOnExit()) {
			charge = this.findParkingChargeByLotIdAndPermitId(lot.getLotId(), car.getOwner());
			if (charge == null) {
				throw new RuntimeException("Parking Charge Not found! Unable to Calculate Hourly Rate.");
			}
			Instant entryTime = charge.getIncurred();

			long hoursBetween = ChronoUnit.HOURS.between(entryTime, exitTime);
			if (hoursBetween < 0) {
				throw new RuntimeException("Invalid parking time detected.");
			}

			ParkingCharge calculatedCharge = calculateParkingCharge(lot, car, entryTime, exitTime);
			charge.setAmount(calculatedCharge.getAmount());
			charge.setIncurred(null);
			lot.getParkedCars().remove(car);
		} else {
			ParkingChargeBuilder parkingChargeBuilder = new ParkingChargeBuilder();
			charge = parkingChargeBuilder.build();
			lot.getParkedCars().remove(car);
		}
		return charge;
	}

	public ParkingCharge calculateParkingCharge(ParkingLot lot, Car car, Instant entryTime, Instant exitTime) {
		ParkingChargeBuilder parkingChargeBuilder = new ParkingChargeBuilder(car.getOwner(), lot.getLotId());

		Money amount;
		amount = calculateBaseCharge(lot, car, entryTime, exitTime);
		for (ParkingStrategy adj : lot.getStrategies()) {
			amount = adj.adjustCharge(amount, lot, car, entryTime, exitTime);
		}

		parkingChargeBuilder.amount(amount);
		if (exitTime != null) {
			parkingChargeBuilder.incurred(entryTime);
		}
		return parkingChargeBuilder.build();
	};

	public Money calculateBaseCharge(ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
		if (exitTime == null) {
			// Entry
			if (parkingLot.getChargeOnExit()) {
				return new Money(0L);
			} else {
				return parkingLot.getLotFee();
			}
		} else {
			// Exit
			if (parkingLot.getChargeOnExit()) {
				long hours = ChronoUnit.HOURS.between(entryTime, exitTime);
				if (hours < 1)
					hours = 1;
				return new Money(parkingLot.getLotFee().getDollars() * hours);
			} else {
				return parkingLot.getLotFee();
			}
		}
	}

	/*
	 * Upon entering a parking lot for the first time, a parking charge will be
	 * created for a car in relation to that lot. If it is an hourly lot there will
	 * be no initial fee. If a car has previously entered the lot, the existing
	 * parking charge will be found and updated. For daily lots this involves adding
	 * an additional daily charge. Customers will be charged multiple times if they
	 * leave and reenter the same daily lot within one day as these are long-term
	 * spots. If this is an hourly lot, the Instant will be captured and added to
	 * the charge to calculate the rate when the car exits the lot.
	 */
	public ParkingCharge createOrUpdateEntryParkingCharge(ParkingLot lot, Car car) {
		ParkingCharge charge = this.findParkingChargeByLotIdAndPermitId(lot.getLotId(), car.getOwner());
		if (charge != null) {
			if (!lot.getChargeOnExit()) {
				// Add daily charge
				Money additionalCharge = calculateParkingCharge(lot, car, Instant.now(), null).getAmount();
				charge.setAmount(addCharge(charge, additionalCharge));
			} else {
				charge.setIncurred(Instant.now());
			}
		} else {
			ParkingChargeBuilder parkingChargeBuilder = new ParkingChargeBuilder(car.getOwner(), lot.getLotId());
			parkingChargeBuilder.incurred(Instant.now());
			if (lot.getChargeOnExit()) {
				Long noInitialFee = 0L;
				parkingChargeBuilder.amount(new Money(noInitialFee));
			} else {
				parkingChargeBuilder.amount(calculateParkingCharge(lot, car, Instant.now(), null).getAmount());
			}
			charge = parkingChargeBuilder.build();
		}

		return charge;

	}

	/*
	 * There is only a single ParkingCharge per permit per lot. LotId and OwnerId
	 * are both unique identifiers pertaining to Parking Lots and Car Permits. Using
	 * both allows specific retrieval of ParkingCharges for updating permit bills.
	 */
	public ParkingCharge findParkingChargeByLotIdAndPermitId(UUID lotId, UUID permitId) {
		return charges.stream()
				.filter(charge -> charge.getLotId().equals(lotId) && charge.getPermitId().equals(permitId)).findFirst()
				.orElse(null);
	}

	public ParkingCharge findParkingChargeByLotIdAndOwnerId(UUID lotId, UUID ownerId) {
		return findParkingChargeByLotIdAndPermitId(lotId, ownerId);
	}

	/*
	 * Using a nightly batch process, this method would be called at midnight to
	 * update the fees for any car still parked in a daily rate parking lot.
	 */
	public void updateDailyFees(List<ParkingLot> lots) {
		for (ParkingLot lot : lots.stream().filter(lot -> !lot.getChargeOnExit()).toList()) {
			for (Car car : lot.getParkedCars()) {
				ParkingCharge charge = this.findParkingChargeByLotIdAndPermitId(lot.getLotId(), car.getOwner());
				Money additionalCharge = calculateParkingCharge(lot, car, Instant.now(), null).getAmount();
				charge.setAmount(addCharge(charge, additionalCharge));
			}
		}
	}

	/*
	 * This method would be called for each car when the University Parking Office
	 * calculates the monthly bill for customers. Since customers can register
	 * multiple cars, the total bill for each car is calculated separately, this is
	 * a hold-over from when compact cars were guaranteed a discounted rate.
	 */
	public Double calculatePermitBill(Car car) {
		Double total = 0.0;
		List<ParkingCharge> carCharges = findParkingChargesByPermitId(car.getOwner());
		for (ParkingCharge charge : carCharges) {
			total += charge.getAmount().getDollars();
		}

		return total;
	}

	/*
	 * Since customers can have multiple cars, the customer bill needs to include
	 * all permits for that customer. The University Parking Office would call this
	 * method to calculate the total monthly bill using the permit bills for each
	 * car registered to the customer and send it to their address. If this process
	 * succeeded, all parking charges would be removed for the given customerId
	 */
	public Boolean calculateCustomerMonthlyBill(Customer customer) {
		try {
			Double total = 0.0;
			for (Car car : customer.getCars()) {
				total += this.calculatePermitBill(car);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Customer Monthly Bill for: ").append(customer.getName()).append("\n");
			sb.append("Bill Amount: $").append(total).append("\n");
			sb.append("Successfuly Sent to: $").append(customer.getAddress().getAddressInfo());
			System.out.println(sb.toString());
			for (Car car : customer.getCars()) {
				removeParkingChargesByPermitId(car.getOwner());
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Failed to Process Customer Monthly Bill: " + e.getMessage());
		}
	}

	/*
	 * Returns updated charge. Used only for Daily Rate lots.
	 */
	public Money addCharge(ParkingCharge parkingCharge, Money lotFee) {

		try {
			Double currentParkingLotChargesInDollars = parkingCharge.getAmount().getDollars();
			Double updatedParkingLotChargesInDollars = currentParkingLotChargesInDollars + lotFee.getDollars();

			Money chargeAmount = new Money(updatedParkingLotChargesInDollars);
			return chargeAmount;
		} catch (Exception e) {
			throw new RuntimeException("Failed to Process Parking Charge: " + e.getMessage());
		}
	}

	/*
	 * Retrieve all Cars matching given customerId. Used for calculating car permit
	 * bill.
	 */
	public List<ParkingCharge> findParkingChargesByPermitId(UUID permitId) {
		return charges.stream().filter(charge -> charge.getPermitId().equals(permitId)).toList();
	}

	public List<ParkingCharge> findParkingChargesByOwnerId(UUID ownerId) {
		return findParkingChargesByPermitId(ownerId);
	}

	/*
	 * Once parking charges are successfully sent to a customer all charges matching
	 * that customer id are removed from the ParkingCharge list.
	 */
	public Boolean removeParkingChargesByPermitId(UUID permitId) {
		return charges.removeIf(charge -> charge.getPermitId().equals(permitId));
	}

	public Boolean removeParkingChargesByOwnerId(UUID ownerId) {
		return removeParkingChargesByPermitId(ownerId);
	}

}
