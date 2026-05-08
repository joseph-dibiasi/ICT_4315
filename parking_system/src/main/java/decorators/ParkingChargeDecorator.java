package decorators;

/* 
 * Abstract Decorator class that implements the ParkingChargeCalculator interface.
 * It overrides the same calculate method as the BaseParkingChargeCalculator,
 * but does not implement the calculate method since it is an abstract class.
 */
public abstract class ParkingChargeDecorator implements ParkingChargeCalculator {

    protected final ParkingChargeCalculator wrappedCalculator;

    protected ParkingChargeDecorator(ParkingChargeCalculator wrappedCalculator) {
        if (wrappedCalculator == null) {
            throw new IllegalArgumentException("Wrapped calculator cannot be null");
        }
        this.wrappedCalculator = wrappedCalculator;
    }
}
