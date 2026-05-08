package decorators;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import models.Car;
import models.Money;
import models.ParkingLot;

/* 
 * BaseParkingChargeCalculator is the default implementation of the ParkingChargeCalculator interface. 
 * It calculates the parking charge based on the parking lot's fee and whether the lot charges on exit or not. 
 * If the lot charges on exit, it calculates the charge based on the number of hours parked, with a minimum of 1 hour. 
 * If the exit time is null and the lot does not charge on exit, it returns the lot fee.
 */
public class BaseParkingChargeCalculator implements ParkingChargeCalculator {

	@Override
	public Money calculate(ParkingLot parkingLot, Car car, Instant entryTime, Instant exitTime) {
		if (exitTime == null) {
			if (parkingLot.getChargeOnExit()) {
				return new Money(0L);
			}
			return parkingLot.getLotFee();
		}

		if (parkingLot.getChargeOnExit()) {
			long hours = ChronoUnit.HOURS.between(entryTime, exitTime);
			if (hours < 1) {
				hours = 1;
			}
			return new Money(parkingLot.getLotFee().getDollars() * hours);
		}

		return parkingLot.getLotFee();
	}
}
