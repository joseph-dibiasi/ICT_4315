package observers;

import models.ParkingCharge;
import models.ParkingEvent;

/**
 * Interface for parking observers. Update method required.
 */
public interface ParkingAction {
    ParkingCharge update(ParkingEvent event);
}
