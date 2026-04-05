package models;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParkingChargeTest {

    @Test
    void testGettersAndSetters() {
        ParkingCharge charge = new ParkingCharge();
        UUID permitId = UUID.randomUUID();
        UUID lotId = UUID.randomUUID();
        Instant incurred = Instant.now();
        Money amount = new Money(150L);

        charge.setPermitId(permitId);
        charge.setLotId(lotId);
        charge.setIncurred(incurred);
        charge.setAmount(amount);

        assertEquals(permitId, charge.getPermitId());
        assertEquals(lotId, charge.getLotId());
        assertEquals(incurred, charge.getIncurred());
        assertEquals(amount, charge.getAmount());
    }

    @Test
    void testToStringEqualsAndHashCode() {
        UUID permitId = UUID.randomUUID();
        UUID lotId = UUID.randomUUID();
        Instant incurred = Instant.parse("2025-01-01T12:00:00Z");
        Money amount = new Money(250L);

        ParkingCharge c1 = new ParkingCharge();
        c1.setPermitId(permitId);
        c1.setLotId(lotId);
        c1.setIncurred(incurred);
        c1.setAmount(amount);

        ParkingCharge c2 = new ParkingCharge();
        c2.setPermitId(permitId);
        c2.setLotId(lotId);
        c2.setIncurred(incurred);
        c2.setAmount(amount);

        assertEquals(c1.toString(), c2.toString());
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertEquals(c1, c1);
        assertNotEquals(c1, null);
        assertNotEquals(c1, "not a charge");

        ParkingCharge c3 = new ParkingCharge();
        c3.setPermitId(UUID.randomUUID());
        assertNotEquals(c1, c3);
    }
}

