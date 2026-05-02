package observers;

import managers.TransactionManager;
import models.ParkingCharge;
import models.ParkingEvent;
import models.ParkingLot;
import models.ParkingOffice;

/**
 * Observer for ParkingLot events. Delegates event handling to TransactionManager.
 */
public class ParkingObserver implements ParkingAction {

    private final TransactionManager transactionManager;

    /**
     * Construct a ParkingObserver.
     */
    public ParkingObserver(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Create ParkingObserver and register it with the office's lots.
     * Registration happens after construction.
     */
    public static ParkingObserver createAndRegister(ParkingOffice office, TransactionManager transactionManager) {
        ParkingObserver observer = new ParkingObserver(transactionManager);
        if (office != null) {
            observer.registerWithOffice(office);
        }
        return observer;
    }

    /**
     * Register this observer with all lots in the provided office.
     * Call this only after the observer is fully constructed.
     */
    public void registerWithOffice(ParkingOffice office) {
        if (office != null) {
            for (ParkingLot lot : office.getLots()) {
                lot.addObserver(this);
            }
        }
    }

    /**
     * Update called by ParkingLot when an event occurs. Returns a ParkingCharge
     * if one was created.
     */
    @Override
    public ParkingCharge update(ParkingEvent event) {
        return transactionManager.park(event);
    }
}
