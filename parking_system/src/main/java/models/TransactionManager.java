package models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This is the class that manages all the parking transactions.
 */
public class TransactionManager {
    private List<ParkingCharge> charges;   

    public TransactionManager() {
        this.charges = new ArrayList<>();
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
			return this.createOrUpdateEntryParkingCharge(lot, car);
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
				charge = this.findParkingChargeByLotIdAndOwnerId(lot.getLotId(), car.getOwner());
				if (charge == null) {
					throw new RuntimeException("Parking Charge Not found! Unable to Calculate Hourly Rate.");
				}
				Instant entryTime = charge.getIncurred();

				Integer hoursBetween = (int) ChronoUnit.HOURS.between(entryTime, exitTime);
				
				if (hoursBetween < 0) {
				    throw new RuntimeException("Invalid parking time detected.");
				}
				
				Double hourlyChargeInDollars = hoursBetween * lot.getLotFee().getDollars();

				Double currentParkingLotChargesInDollars = charge.getAmount().getDollars();
				Double updatedParkingLotChargesInDollars = currentParkingLotChargesInDollars + hourlyChargeInDollars;

				Money chargeAmount = new Money(updatedParkingLotChargesInDollars);
				charge.setAmount(chargeAmount);
				charge.setIncurred(null);
				lot.getParkedCars().remove(car);
			} else {
				charge = new ParkingCharge();
				lot.getParkedCars().remove(car);
			}
		return charge;
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
		ParkingCharge charge = this.findParkingChargeByLotIdAndOwnerId(lot.getLotId(), car.getOwner());
		Money lotFee = lot.getLotFee();
		if (charge != null) {
			if (!lot.getChargeOnExit()) {

				charge.setAmount(addCharge(charge, lotFee));
			} else {
				charge.setIncurred(Instant.now());
			}
		} else {
			charge = new ParkingCharge();
			charge.setLotId(lot.getLotId());
			charge.setPermitId(car.getOwner());
			charge.setIncurred(Instant.now());
			if (lot.getChargeOnExit()) {
				Long noInitialFee = 0L;
				charge.setAmount(new Money(noInitialFee));
			} else {
				charge.setAmount(lot.getLotFee());
			}
		}
		
		return charge;

	}
	
	/*
	 * There is only a single ParkingCharge per permit per lot. LotId and OwnerId
	 * are both unique identifiers pertaining to Parking Lots and Car Permits. Using
	 * both allows specific retrieval of ParkingCharges for updating permit bills.
	 */
	public ParkingCharge findParkingChargeByLotIdAndOwnerId(UUID lotId, UUID ownerId) {
		return charges.stream()
				.filter(charge -> charge.getLotId().equals(lotId) && charge.getPermitId().equals(ownerId)).findFirst()
				.orElse(null);
	}


	/*
	 * Using a nightly batch process, this method would be called at midnight to
	 * update the fees for any car still parked in a daily rate parking lot.
	 */
	public void updateDailyFees(List<ParkingLot> lots) {
		for (ParkingLot lot : lots.stream().filter(lot -> !lot.getChargeOnExit()).toList()) {
			for (Car car : lot.getParkedCars()) {
				ParkingCharge charge = this.findParkingChargeByLotIdAndOwnerId(lot.getLotId(), car.getOwner());
				charge.setAmount(addCharge(charge, lot.getLotFee()));
			}
		}		
	}

	/*
	 * This method would be called for each car when the University Parking Office
	 * calculates the monthly bill for customers. Since customers can register
	 * multiple cars, the total bill for each car is calculated separately. This
	 * allows the 20% compact car discount to apply on a car by car basis.
	 */
	public Double calculatePermitBill(Car car) {
		Double total = 0.0;
		List<ParkingCharge> charges = findParkingChargesByOwnerId(car.getOwner());
		for (ParkingCharge charge : charges) {
			total += charge.getAmount().getDollars();
		}
		if (car.getType() == CarType.COMPACT) {
			total = total * 0.8;
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
			return removeParkingChargesByOwnerId(customer.getCustomerId());
		} catch (Exception e) {
			throw new RuntimeException("Failed to Process Customer Monthly Bill: " + e.getMessage());
		}
	}
	
	/*
	 * Returns updated charge. Used only for Daily Rate lots.
	 */
	public Money addCharge(ParkingCharge parkingCharge, Money lotFee ) {

		try {
			Double currentParkingLotChargesInDollars = parkingCharge.getAmount().getDollars();
			Double updatedParkingLotChargesInDollars = currentParkingLotChargesInDollars
					+ lotFee.getDollars();

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
	public List<ParkingCharge> findParkingChargesByOwnerId(UUID ownerId) {
		return charges.stream().filter(charge -> charge.getPermitId().equals(ownerId)).toList();
	}

	/*
	 * Once parking charges are successfully sent to a customer all charges matching
	 * that customer id are removed from the ParkingCharge list.
	 */
	public Boolean removeParkingChargesByOwnerId(UUID ownerId) {
		return charges.removeIf(charge -> charge.getPermitId().equals(ownerId));
	}

}
