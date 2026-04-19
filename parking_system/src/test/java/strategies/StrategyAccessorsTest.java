package strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import models.Money;

class StrategyAccessorsTest {

    @Test
    void carTypeStrategyGettersSetters() {
        CarTypeStrategy s = new CarTypeStrategy();
        s.setRateModifier(0.75);
        assertEquals(0.75, s.getRateModifier(), 0.0001);
    }

    @Test
    void dayOfWeekStrategyGettersSetters() {
        DayOfWeekStrategy s = new DayOfWeekStrategy();
        s.setWeekendModifier(0.5);
        assertEquals(0.5, s.getWeekendModifier(), 0.0001);
    }

    @Test
    void timeOfDayStrategyGettersSetters() {
        TimeOfDayStrategy s = new TimeOfDayStrategy();
        s.setPeakSurcharge(2.5);
        s.setSpecialDiscount(0.1);
        assertEquals(2.5, s.getPeakSurcharge(), 0.0001);
        assertEquals(0.1, s.getSpecialDiscount(), 0.0001);
    }

    @Test
    void specialDaysStrategyGettersSetters() {
        SpecialDaysStrategy s = new SpecialDaysStrategy();
        s.setSpecialDiscount(0.33);
        assertEquals(0.33, s.getSpecialDiscount(), 0.0001);
    }

    @Test
    void parkingLotSetStrategiesStoresList() {
        models.ParkingLot lot = new models.ParkingLot();
        java.util.List<ParkingStrategy> list = new java.util.ArrayList<>();
        list.add(new CarTypeStrategy());
        lot.setStrategies(list);
        assertTrue(lot.getStrategies().contains(list.get(0)));
    }
}
