package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import models.ParkingEvent.EventType;
import strategies.ParkingStrategy;

public class ParkingLot {

    // Parking Lot ID. Unique.
    private UUID lotId;

    private Address address;

    private Integer capacity;

    // True = Hourly Rate. False = Daily Rate.
    private Boolean chargeOnExit;

    private Money lotFee;

    private Set<Car> parkedCars;

    // Strategies are implemented as a list to allow maximum flexibility in how many are applied per lot. 
    private List<ParkingStrategy> strategies;

    // Observers that wish to be notified of parking events for this lot
    private List<observers.ParkingAction> observers;

    public UUID getLotId() {
        return lotId;
    }

    public void setLotId(UUID lotId) {
        this.lotId = lotId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "ParkingLot [lotId=" + lotId + ", address=" + address + ", capacity=" + capacity + "]";
    }

    public Boolean getChargeOnExit() {
        return chargeOnExit;
    }

    public void setChargeOnExit(Boolean chargeOnExit) {
        this.chargeOnExit = chargeOnExit;
    }

    public Money getLotFee() {
        return lotFee;
    }

    public void setLotFee(Money lotFee) {
        this.lotFee = lotFee;
    }

    public Set<Car> getParkedCars() {
        if (parkedCars == null) {
            setParkedCars(new HashSet<>());
        }
        return parkedCars;
    }

    public void setParkedCars(Set<Car> parkedCars) {
        this.parkedCars = parkedCars;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, capacity, chargeOnExit, lotFee, lotId, parkedCars, strategies);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ParkingLot other = (ParkingLot) obj;
        return Objects.equals(address, other.address) && Objects.equals(capacity, other.capacity)
                && Objects.equals(chargeOnExit, other.chargeOnExit) && Objects.equals(lotFee, other.lotFee)
                && Objects.equals(lotId, other.lotId) && Objects.equals(parkedCars, other.parkedCars)
                && Objects.equals(strategies, other.strategies);
    }

    public List<ParkingStrategy> getStrategies() {
        if (strategies == null) {
            strategies = new ArrayList<>();
        }
        return strategies;
    }

    public void setStrategies(List<ParkingStrategy> strategies) {
        this.strategies = strategies;
    }

    public void addObserver(observers.ParkingAction observer) {
        if (observer == null) return;
        if (observers == null) observers = new CopyOnWriteArrayList<>();
        observers.add(observer);
    }

    public void removeObserver(observers.ParkingAction observer) {
        if (observer == null || observers == null) return;
        observers.remove(observer);
    }

    /**
     * Notify observers of a generic parking event and return the first non-null
     * ParkingCharge created by an observer.
     */
    public ParkingCharge notifyObservers(ParkingEvent event) {
        if (observers == null) {
            throw new IllegalArgumentException("Observers not found!");
        }
        for (observers.ParkingAction obs : observers) {
            ParkingCharge charge = obs.update(event);
            if (charge != null) return charge;
        }
        return null;
    }

    /**
     * Simulate a car entering this lot. This updates the parked cars and
     * notifies observers.
     */
    public ParkingCharge park(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        if (this.parkedCars == null) {
            this.parkedCars = new HashSet<>();
        }
        ParkingEvent event = new ParkingEvent(EventType.ENTRY, LocalDateTime.now(), this, car);
        // Let observers (TransactionManager) process and return ParkingCharge
        ParkingCharge charge = notifyObservers(event);
        // if observer didn't add the car/charge, default local behavior
        if (charge == null) {
            this.parkedCars.add(car);
        }
        return charge;
    }

    /**
     * Overloaded park method which accepts an explicit entry time.
     */
    public ParkingCharge park(LocalDateTime date, Car car) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        if (this.parkedCars == null) {
            this.parkedCars = new HashSet<>();
        }
        ParkingEvent event = new ParkingEvent(EventType.ENTRY, date, this, car);
        ParkingCharge charge = notifyObservers(event);
        if (charge == null) {
            this.parkedCars.add(car);
        }
        return charge;
    }

    /**
     * Simulate a car leaving this lot. Notifies observers.
     */
    public ParkingCharge leave(LocalDateTime date, Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        ParkingEvent event = new ParkingEvent(EventType.EXIT, date, this, car);
        ParkingCharge charge = notifyObservers(event);
        if (charge == null && this.parkedCars != null) {
            this.parkedCars.remove(car);
        }
        return charge;
    }

}
